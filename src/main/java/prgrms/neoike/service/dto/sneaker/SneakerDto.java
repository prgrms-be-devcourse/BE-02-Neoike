package prgrms.neoike.service.dto.sneaker;

import lombok.Builder;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.SneakerCategory;

import java.time.LocalDateTime;

public record SneakerDto(
    MemberCategory memberCategory,
    SneakerCategory sneakerCategory,
    String name,
    int price,
    String description,
    String code,
    LocalDateTime releaseDate
) {

    @Builder
    public SneakerDto {
    }
}
