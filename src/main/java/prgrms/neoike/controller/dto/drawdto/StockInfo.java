package prgrms.neoike.controller.dto.drawdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record StockInfo(
    @NotNull
    @PositiveOrZero
    int size,

    @NotNull
    @PositiveOrZero
    int quantity
) {

}
