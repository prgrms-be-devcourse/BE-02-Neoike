package prgrms.neoike.controller.dto.drawdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

public record DrawSaveRequest(
        @NotNull
        Long sneakerId,

        @NotNull
        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,

        @NotNull
        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,

        @NotNull
        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime winningDate,

        @NotNull
        List<ItemSizeAndQuantity> sneakerItems,

        @NotNull
        @PositiveOrZero
        int quantity
) {
    @Builder
    public DrawSaveRequest {
    }
}
