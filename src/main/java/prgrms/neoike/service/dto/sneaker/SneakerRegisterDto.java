package prgrms.neoike.service.dto.sneaker;

import lombok.Builder;

import java.util.List;

public record SneakerRegisterDto(
    List<SneakerImageDto> imageDtos,
    SneakerDto sneakerDto,
    List<SneakerStockDto> stockDtos
) {

    @Builder
    public SneakerRegisterDto {
    }
}
