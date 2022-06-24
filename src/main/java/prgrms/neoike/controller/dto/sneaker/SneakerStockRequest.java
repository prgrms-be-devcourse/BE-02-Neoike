package prgrms.neoike.controller.dto.sneaker;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerSize;

import javax.validation.constraints.PositiveOrZero;

public record SneakerStockRequest(
    @SneakerSize
    int size,

    @PositiveOrZero
    int quantity
) {

}
