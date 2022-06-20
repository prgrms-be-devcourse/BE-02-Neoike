package prgrms.neoike.controller.dto.drawdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.ArrayList;

public record DrawSaveRequest(
        @NotNull
        Long sneakerId,

        @NotNull
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate,

        @NotNull
        LocalDateTime winningDate,

        @NotNull
        @Positive
        int quantity,

        @NotNull
        ArrayList<DrawItem> sneakerItems
) {
}
