package prgrms.neoike.controller.dto.sneaker.request;

import javax.validation.Valid;
import java.util.List;

public record SneakerRegisterRequest(
    @Valid
    List<SneakerImageRequest> sneakerImages,

    @Valid
    SneakerRequest sneaker,

    @Valid
    List<SneakerStockRequest> sneakerStocks
) {

}
