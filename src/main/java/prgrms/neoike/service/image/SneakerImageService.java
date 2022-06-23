package prgrms.neoike.service.image;

import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageUploadDto;

public interface SneakerImageService {

    SneakerImageResponse upload(SneakerImageUploadDto uploadDto);
}
