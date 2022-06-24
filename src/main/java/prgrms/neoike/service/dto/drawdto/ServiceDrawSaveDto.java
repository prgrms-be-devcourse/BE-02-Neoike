package prgrms.neoike.service.dto.drawdto;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

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
        int quantity
) {
    @Builder
    public ServiceDrawSaveDto {
    }
}
