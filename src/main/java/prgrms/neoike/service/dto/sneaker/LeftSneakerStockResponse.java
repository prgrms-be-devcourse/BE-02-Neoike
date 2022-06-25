package prgrms.neoike.service.dto.sneaker;

public record LeftStockResponse(
    Long stockId,
    int size,
    int leftStock,
    Long sneakerId
) {

}
