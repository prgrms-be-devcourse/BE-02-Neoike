package prgrms.neoike.controller.dto.drawdto;

import java.time.LocalDateTime;
import java.util.ArrayList;

public record DrawSaveRequest(
        Long sneakerId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime winningDate,
        int quantity,

        ArrayList<DrawItem> sneakerItems
) {
}
