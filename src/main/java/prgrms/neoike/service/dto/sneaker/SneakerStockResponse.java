package prgrms.neoike.service.dto.sneaker;

public record SneakerStockResponse(
    Long stockId,
    int size,
    int quantity,
    Long sneakerId
) {

}
