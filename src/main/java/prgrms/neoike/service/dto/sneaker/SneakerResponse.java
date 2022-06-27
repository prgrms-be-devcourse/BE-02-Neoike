package prgrms.neoike.service.dto.sneaker;

import lombok.Builder;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.SneakerCategory;

import java.time.LocalDateTime;
import java.util.List;

public record SneakerResponse(
    Long sneakerId,
    MemberCategory memberCategory,
    SneakerCategory sneakerCategory,
    String name,
    int price,
    String description,
    String code,
    LocalDateTime releaseDate,
    List<String> imagePaths
) {

    @Builder
    public SneakerResponse {
    }
}
