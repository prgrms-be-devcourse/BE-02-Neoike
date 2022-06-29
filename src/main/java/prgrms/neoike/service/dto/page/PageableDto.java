package prgrms.neoike.service.dto.page;

public record PageableDto(
    String page,
    String size,
    String sortBy,
    String direction
) {

}
