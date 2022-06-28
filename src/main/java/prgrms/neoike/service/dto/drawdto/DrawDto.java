package prgrms.neoike.service.dto.drawdto;

import java.time.LocalDateTime;
import lombok.Builder;

public record DrawDto(
    Long drawId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    LocalDateTime winningDate,
    int quantity,
    Long sneakerId,
    String sneakerName,
    String sneakerThumbnailPath
) {

    @Builder
    public DrawDto {
    }
}