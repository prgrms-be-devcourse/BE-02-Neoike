package prgrms.neoike.controller.dto.drawdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public record DrawItem(
        @NotNull
        Long sneakerItemId,

        @NotNull
        @PositiveOrZero
        int quantity
) {
}
