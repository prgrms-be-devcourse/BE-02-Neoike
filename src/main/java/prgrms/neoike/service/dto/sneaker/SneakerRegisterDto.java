package prgrms.neoike.service.dto.sneaker;

import java.util.List;

public record SneakerRegisterDto(
    List<String> imageDto,
    SneakerDto sneakerDto,
    List<SneakerStockDto> stockDto
) {

}
