package prgrms.neoike.domain.sneaker.manager.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prgrms.neoike.common.exception.FailedStoreImageException;
import prgrms.neoike.domain.sneaker.Image;
import prgrms.neoike.domain.sneaker.SneakerImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.util.UUID.randomUUID;


@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class ImageFileManager {

    @Value("${sneaker.images.dir}")
    private String dir;

    private final Decoder decoder = Base64.getDecoder();

    private final ImageFileValidator imageFileValidator;

    public Set<SneakerImage>  storeImageFileToLocal(List<ImageFile> imageFiles) {
        imageFileValidator.validateImageFiles(imageFiles);

        Set<SneakerImage> sneakerImages = new HashSet<>();

        imageFiles
            .forEach(
                imageFile -> {
                    imageFileValidator.validateEncodedImageFile(imageFile.encodedName());
                    imageFileValidator.validateImageFileFormat(imageFile.originName());

                    String newImageFileName = createNewImageFileName(imageFile.originName());
                    File createdImageFile = createImageFile(newImageFileName);
                    byte[] decodedBytes = decode64(imageFile.encodedName());
                    storeImageFile(createdImageFile, decodedBytes);

                    sneakerImages.add(
                        new SneakerImage(
                            new Image(dir, imageFile.originName(), newImageFileName)
                        )
                    );
                }
            );

        return sneakerImages;
    }

    private File createImageFile(String newImageFileName) {
        return new File(dir + newImageFileName);
    }

    private String createNewImageFileName(String originName) {
        int dotIndex = originName.lastIndexOf(".");

        return randomUUID() + originName.substring(dotIndex).toUpperCase();
    }

    private byte[] decode64(String encodedName) {
        return decoder.decode(encodedName);
    }

    private void storeImageFile(File file, byte[] decodedBytes) {
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file)
        ) {
            fileOutputStream.write(decodedBytes);
        } catch (IOException e) {
            log.warn("입출력 예외가 발생하였습니다. : {}", e.getMessage(), e);

            throw new FailedStoreImageException(
                format(
                    "이미지 파일을 저장하는데 실패하였습니다. (URI: {0})", file.toURI()
                )
            );
        }
    }
}
