package prgrms.neoike.controller.dto.sneaker;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.SneakerCategory;
import prgrms.neoike.domain.sneaker.validators.annotation.SneakerCode;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public record SneakerRequest(
    MemberCategory memberCategory,

    SneakerCategory sneakerCategory,

    @NotBlank
    @Size(max = 50)
    String name,

    @PositiveOrZero
    int price,

    @NotBlank
    String description,

    @SneakerCode(length = 10, message = "상품 코드의 형식이 잘못되었습니다. (입력값: ${validatedValue})")
    String code,

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime releaseDate
) {

    @Builder
    public SneakerRequest {
    }
}
