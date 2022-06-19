package prgrms.neoike.domain.draw;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.common.exception.TimeSequnceException;
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
    private int quantity;

    @Builder
    public Draw(
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime winningDate,
            int quantity
    ) {
        validateTimeOrder(startDate, endDate, winningDate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningDate = winningDate;
        this.quantity = quantity;
    }

    private void validateTimeOrder(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime winningDate) {
        boolean startAndEndCompare = startDate.isBefore(endDate);
        boolean endAndWinningCompare = endDate.isBefore(winningDate);

        if (!(startAndEndCompare && endAndWinningCompare)) {
            throw new TimeSequnceException("입력된 Date 날짜의 순서가 맞지 않습니다.");
        }
    }

    public boolean checkPossibility() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}