package prgrms.neoike.domain.sneaker.manager.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import prgrms.neoike.common.exception.InvalidInputValueException;

import java.util.List;

import static java.lang.Math.round;
import static java.text.MessageFormat.format;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "image.file")
public class ImageFileValidator {

    private String maxSize;
    private int mbUnit;
    private double overHead;

    public void validateImageFiles(List<ImageFile> imageFiles) {
        if (imageFiles.isEmpty()) {
            throw new InvalidInputValueException("이미지 파일이 입력되지 않았습니다.");
        }
    }

    public void validateEncodedImageFile(String encodedName) {
        validateName(encodedName);
        validateSize(encodedName);
    }

    public void validateImageFileFormat(String originName) {
        String fileFormat = originName.substring(originName.lastIndexOf(".") + 1).toLowerCase();
        if (!fileFormat.equals("png") && !fileFormat.equals("jpg") && !fileFormat.equals("jpeg")) {
            throw new InvalidInputValueException("지원하지 않는 이미지 파일 포맷입니다. (지원목록: *.png, *.jpg, *.jpeg)");
        }

    }

    private void validateName(String encodedName) {
        if (encodedName.isEmpty() || encodedName.isBlank()) {
            throw new InvalidInputValueException("이미지 파일의 이름이 비어있습니다.");
        }
    }

    private void validateSize(String encodedName) {
        if (encodedName.length() > toLength(maxSize)) {
            throw new InvalidInputValueException(
                format(
                    "이미지 파일의 크기가 너무 큽니다. (이미지 크기: {0})", toMB(encodedName.length())
                )
            );
        }
    }

    private String toMB(int length) {
        return format("{0}MB", length / (mbUnit * overHead));
    }

    private int toLength(String maxSize) {
        int mbSize = Integer.parseInt(maxSize.substring(0, maxSize.indexOf("M")));

        return (int) round(mbSize * mbUnit * overHead);
    }
}
