package prgrms.neoike.domain.draw;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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
    @Positive
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
            throw new IllegalArgumentException("입력된 quantity 가 음수 입니다.");
        }
    }

    private void validateTimeOrder(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime winningDate) {
        boolean isStartBeforeEnd = startDate.isBefore(endDate);
        boolean isEndBeforeWinning = endDate.isBefore(winningDate);

        if (!(isStartBeforeEnd && isEndBeforeWinning)) {
            throw new IllegalArgumentException("입력된 Date 날짜의 순서가 맞지 않습니다.");
        }
    }

    public boolean drawAndCheckSpare() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}