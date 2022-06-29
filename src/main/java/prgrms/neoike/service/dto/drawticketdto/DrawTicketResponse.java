package prgrms.neoike.service.dto.drawticketdto;

import lombok.Builder;
import prgrms.neoike.domain.draw.DrawStatus;

public record DrawTicketResponse(
        Long drawTicketId,
        DrawStatus drawStatus,
        String sneakerName,
        int price,
        String code,
        int size
) {
    @Builder
    public DrawTicketResponse {
    }
}
