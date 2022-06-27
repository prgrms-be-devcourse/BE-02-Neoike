package prgrms.neoike.service.image;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static prgrms.neoike.service.image.ImageFileValidator.validateEmptyFile;
import static prgrms.neoike.service.image.ImageFileValidator.validateFileFormat;

class ImageFileValidatorTest {

    private File emptyFile;
    private String fileName;
    private String contentType;

    @BeforeEach
    void setup () throws IOException {
        this.fileName = "empty";
        this.contentType = "PNG";
        String path = System.getProperty("user.dir") + "/src/test/resources/" + fileName + "." + contentType;
        this.emptyFile = new File(path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(emptyFile));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(emptyFile.getAbsolutePath()));
    }

    @Test
    @DisplayName("비어있는 이미지 파일 리스트가 업로드 되면 예외가 발생한다.")
    void testEmptyImageFiles() throws IOException {
        MockMultipartFile multipartFile = getMockMultipartFile(fileName, contentType, emptyFile.getAbsolutePath());

        assertThrows(IllegalArgumentException.class, () -> validateEmptyFile(multipartFile));
    }

    @DisplayName("지원하지 않는 이미지 파일 포맷이 업로드 되면 예외가 발생한다.")
    @ParameterizedTest(name = "이미지 파일 포맷 테스트 {index}")
    @MethodSource("testNotSupportFormatTypeSource")
    void testNotSupportFormatType(String formatType) {
        assertThrows(IllegalArgumentException.class, () -> validateFileFormat(formatType, List.of(".PNG", ".JPG", ".JPEG")));
    }

    static List<String> testNotSupportFormatTypeSource() {
        return List.of(
            ".exe",
            ".json",
            ".zip",
            ".text",
            ".class",
            ".jar"
        );
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);

        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fis);
    }
}