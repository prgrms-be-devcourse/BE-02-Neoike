package prgrms.neoike.service.image;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageUploadDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Path.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ImageLocalServiceTest {

    @Autowired
    ImageLocalService imageLocalService;

    private static SneakerImageResponse imageResponse;

    @AfterAll
    static void cleanup() throws IOException {
        List<String> paths = imageResponse.paths();

        for (String path : paths) {
            deleteIfExists(of(System.getProperty("user.dir") + path));
        }
    }

    @Test
    @DisplayName("이미지 파일을 저장한다.")
    void testStoreImageToLocal() throws IOException {
        String fullPath = System.getProperty("user.dir") + "/src/test/resources/test.PNG";
        MockMultipartFile multipartFile = getMockMultipartFile("test", "PNG", fullPath);
        imageResponse = imageLocalService.upload(new SneakerImageUploadDto(List.of(multipartFile)));

        Assertions.assertAll(
            () -> assertThat(imageResponse).isNotNull(),
            () -> imageResponse
                .paths()
                .forEach(
                    path -> assertThat(new File(System.getProperty("user.dir") + path)).isFile()
                )
        );
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fis = new FileInputStream(new File(path));

        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fis);
    }
}