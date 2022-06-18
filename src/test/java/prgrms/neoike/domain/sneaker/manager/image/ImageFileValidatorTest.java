package prgrms.neoike.domain.sneaker.manager.image;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Base64Utils;
import prgrms.neoike.common.exception.InvalidInputValueException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ImageFileValidatorTest {

    @Autowired
    ImageFileValidator imageFileValidator;

    @Test
    @DisplayName("비어있는 이미지 파일 리스트가 업로드 되면 예외가 발생한다.")
    void testEmptyImageFiles() {
        List<ImageFile> imageFiles = new ArrayList<>();

        assertThrows(InvalidInputValueException.class, () -> imageFileValidator.validateImageFiles(imageFiles));
    }

    @DisplayName("인코딩 된 이미지 파일의 이름이 비어있으면 예외가 발생한다.")
    @ParameterizedTest(name = "인코딩 파일 이름 테스트 {index}")
    @MethodSource("testBlankImageFileNameSource")
    void testBlankImageFileName(String encodedName) {
        assertThrows(InvalidInputValueException.class, () -> imageFileValidator.validateEncodedImageFile(encodedName));
    }

    static List<String> testBlankImageFileNameSource() {
        return List.of(
            "",
            "    ",
            " "
        );
    }

    @DisplayName("지원하지 않는 이미지 파일 포맷이 업로드 되면 예외가 발생한다.")
    @ParameterizedTest(name = "이미지 파일 포맷 테스트 {index}")
    @MethodSource("testNotSupportedImageFileSource")
    void testNotSupportedImageFile(String originName) {
        assertThrows(InvalidInputValueException.class, () -> imageFileValidator.validateImageFileFormat(originName));
    }

    static List<String> testNotSupportedImageFileSource() {
        return List.of(
            "abc.exe",
            "abc.json",
            "abc.zip",
            "abc.text",
            "abc.class",
            "abc.jar"
        );
    }

    @Test
    @DisplayName("최대 사이즈 보다 큰 용량의 이미지 파일이 업로드되면 예외가 발생한다.")
    void testGreaterThanMaxSize() throws IOException {
        var imageBytes = extractBytes();
        var baseBytes = Base64Utils.encodeUrlSafe(requireNonNull(imageBytes));
        String encodedName = new String(baseBytes);

        assertThrows(
            InvalidInputValueException.class,
            () -> imageFileValidator.validateEncodedImageFile(encodedName)
        );
    }

    public static byte[] extractBytes() {
        File imageFile = new File("src/test/resources/bigImage.PNG");

        try (
            FileInputStream fis = new FileInputStream(imageFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}