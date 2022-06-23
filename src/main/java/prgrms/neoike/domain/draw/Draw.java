package prgrms.neoike.domain.draw;

import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

@Getter
@Entity
@Table(name = "draw")
@NoArgsConstructor(access = PROTECTED)
public class Draw extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "start_date")
    @NotNull
    private LocalDateTime startDate;

    @Column(name = "end_date")
    @NotNull
    private LocalDateTime endDate;

    @Column(name = "winning_date")
    @NotNull
    private LocalDateTime winningDate;

    @Column(name = "quantity")
    @NotNull
    @PositiveOrZero
    private int quantity;

    @Builder
    public Draw(
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime winningDate,
            int quantity
    ) {
        validateTimeOrder(startDate, endDate, winningDate);
        validateQuantity(quantity);
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningDate = winningDate;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("입력된 수량이 음수 입니다.");
        }
    }

    private void validateTimeOrder(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime winningDate) {
        boolean isStartBeforeEnd = startDate.isBefore(endDate);
        boolean isEndBeforeWinning = endDate.isBefore(winningDate);

        if (!(isStartBeforeEnd)) {
            throw new IllegalArgumentException(format("입력된 날짜의 순서가 맞지 않습니다. (startDate : {0} , endDate : {1})", startDate, endDate));
        }
        if (!isEndBeforeWinning) {
            throw new IllegalArgumentException(format("입력된 날짜의 순서가 맞지 않습니다. (endDate : {0} , winningDate : {1})", endDate, winningDate));
        }
    }

    public boolean validateSpare() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        throw new IllegalStateException(format("draw 의 수량이 0 이어서 더이상 ticket 발행이 안됩니다. drawId : {0}", id));
    }
}