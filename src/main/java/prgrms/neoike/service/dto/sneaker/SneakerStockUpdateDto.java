package prgrms.neoike.service.dto.sneaker;

public record SneakerStockUpdateDto(
    Long stockId,
    int size,
    int quantity
) {

}
