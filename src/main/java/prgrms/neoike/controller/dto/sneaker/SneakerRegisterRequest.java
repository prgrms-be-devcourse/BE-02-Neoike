package prgrms.neoike.controller.dto.sneaker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public record SneakerRegisterRequest(
    @NotNull
    List<String> imagePaths,

    @Valid
    SneakerRequest sneaker,

    @Valid
    List<SneakerStockRequest> sneakerStocks
) {

}
