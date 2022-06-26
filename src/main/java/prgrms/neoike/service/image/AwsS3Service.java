package prgrms.neoike.service.image;

import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageUploadDto;

public class AwsS3Service implements SneakerImageService {

    @Override
    public SneakerImageResponse upload(SneakerImageUploadDto uploadDto) {
        //추후에 aws 학습 후 구현

        return null;
    }
}
