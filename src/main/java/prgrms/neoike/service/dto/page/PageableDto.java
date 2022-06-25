package prgrms.neoike.service.dto.page;

public record PageableDto(
    int page,
    int size,
    String sortBy
) {

}
