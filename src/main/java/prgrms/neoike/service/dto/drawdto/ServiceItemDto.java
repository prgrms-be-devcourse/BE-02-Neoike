package prgrms.neoike.service.dto.drawdto;

import javax.validation.constraints.NotNull;

public record ServiceItemDto(
        @NotNull
        int size,

        @NotNull
        int quantity
){
}
