package prgrms.neoike.service.dto.drawdto;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

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
        List<ServiceItemDto> sneakerItems,

        @NotNull
        @PositiveOrZero
        int quantity
) {
    @Builder
    public ServiceDrawSaveDto {
    }
}
