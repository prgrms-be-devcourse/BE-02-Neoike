package prgrms.neoike.controller.dto.sneaker;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerSize;

public record SneakerStockRequest(
    @SneakerSize(message = "잘못된 형식의 신발 사이즈가 입력되었습니다. (입력값: ${validatedValue})")
    int size,

    int quantity
) {

}
