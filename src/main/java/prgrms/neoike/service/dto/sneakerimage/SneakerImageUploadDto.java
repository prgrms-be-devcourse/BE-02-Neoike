package prgrms.neoike.service.dto.sneakerimage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SneakerImageUploadDto(
    List<MultipartFile> files
) {

}
