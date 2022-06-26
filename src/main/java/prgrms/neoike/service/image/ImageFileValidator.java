package prgrms.neoike.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.join;

public class ImageFileValidator {

    public static void validateEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 입력되지 않았습니다.");
        }
    }

    public static void validateFileFormat(String fileFormat, List<String> formatTypes) {
        List<String> matchTypes = formatTypes
            .stream()
            .filter(type -> type.equalsIgnoreCase(fileFormat))
            .toList();

        if (matchTypes.isEmpty()) {
            throw new IllegalArgumentException(
                format("지원하지 않는 파일 포맷입니다. (지원목록: {})", join(formatTypes, ", "))
            );
        }
    }
}
