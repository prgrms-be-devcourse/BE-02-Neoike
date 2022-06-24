package prgrms.neoike.service.dto.sneaker;

import java.util.List;

public record SneakerDetailResponse(
    List<SneakerStockResponse> sneakerStocks,
    SneakerResponse sneaker
) {

}
