package prgrms.neoike.controller.dto.drawdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record DrawItem(
        @NotNull
        Long sneakerItemId,

        @NotNull
        @Positive
        int quantity
) {
}
