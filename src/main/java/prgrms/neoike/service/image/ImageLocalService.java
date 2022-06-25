package prgrms.neoike.service.image;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageUploadDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static prgrms.neoike.service.image.ImageFileValidator.validateEmptyFile;
import static prgrms.neoike.service.image.ImageFileValidator.validateFileFormat;

@Getter
@Service
public class ImageLocalService implements SneakerImageService {

    @Value("${sneaker.images.formatTypes}")
    private List<String> formatTypes;

    @Value("${sneaker.images.path}")
    private String path;

    @Override
    public SneakerImageResponse upload(SneakerImageUploadDto uploadDto) {
        List<MultipartFile> files = uploadDto.files();
        List<String> paths = new ArrayList<>();

        for (MultipartFile file : files) {
            validateEmptyFile(file);

            String fileFormat = getFileFormat(requireNonNull(file.getOriginalFilename()));
            validateFileFormat(fileFormat, formatTypes);

            String fileName = rename(fileFormat);
            String fullPath = path + fileName;
            File newFile = new File(System.getProperty("user.dir") + fullPath);

            storeFile(file, newFile);

            paths.add(fullPath);
        }

        return new SneakerImageResponse(paths);
    }

    private void storeFile(MultipartFile file, File newFile) {
        try {
            file.transferTo(newFile);
        } catch (IOException ex) {
            throw new IllegalStateException("이미지를 저장하는데 실패하였습니다.", ex);
        }
    }

    private String rename(String fileFormat) {
        return now() + fileFormat;
    }

    private String getFileFormat(String originName) {
        int lastDotIndex = originName.lastIndexOf(".");

        return originName.substring(lastDotIndex).toUpperCase();
    }
}
