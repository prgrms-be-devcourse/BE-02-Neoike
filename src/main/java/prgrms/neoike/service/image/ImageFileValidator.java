package prgrms.neoike.service.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import prgrms.neoike.service.dto.sneaker.SneakerImageDto;

import java.util.List;
import java.util.Set;

import static java.lang.Math.round;
import static java.lang.String.join;
import static java.text.MessageFormat.format;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "image.file")
public class ImageFileValidator {

    private String maxSize;
    private int mbUnit;
    private double overHead;
    private Set<String> supportFormatTypes;

    public void validateImageFiles(List<SneakerImageDto> imageDtos) {
        if (imageDtos.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 입력되지 않았습니다.");
        }
    }

    public void validateEncodedImageFile(String encodedName) {
        validateName(encodedName);
        validateSize(encodedName);
    }

    public void validateImageFileFormat(String originName) {
        String fileFormat = originName.substring(originName.lastIndexOf(".") + 1).toUpperCase();

        if (!supportFormatTypes.contains(fileFormat)) {
            throw new IllegalArgumentException(
                format("지원하지 않는 이미지 파일 포맷입니다. (지원포맷: {0})",
                    join(", ", supportFormatTypes)
                )
            );
        }
    }

    private void validateName(String encodedName) {
        if (encodedName.isEmpty() || encodedName.isBlank()) {
            throw new IllegalArgumentException("이미지 파일의 이름이 비어있습니다.");
        }
    }

    private void validateSize(String encodedName) {
        if (encodedName.length() > toLength(maxSize)) {
            throw new IllegalStateException(
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
