package prgrms.neoike.controller.dto.drawdto;

import javax.validation.constraints.NotNull;

public record ItemSizeAndQuantity(
        @NotNull
        int size,

        @NotNull
        int quantity
        ) {
}
