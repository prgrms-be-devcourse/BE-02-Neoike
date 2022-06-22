package prgrms.neoike.service.dto.drawdto;

import lombok.Builder;
import prgrms.neoike.controller.dto.drawdto.DrawItem;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;

public record ServiceDrawSaveDto(
        @NotNull
        Long sneakerId,

        @NotNull
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate,

        @NotNull
        LocalDateTime winningDate,

        @NotNull
        @PositiveOrZero
        int quantity,

        @NotNull
        ArrayList<DrawItem> sneakerItems
) {
    @Builder
    public ServiceDrawSaveDto {
    }
}
