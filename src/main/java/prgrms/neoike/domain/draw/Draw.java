package prgrms.neoike.domain.draw;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;
import prgrms.neoike.domain.sneaker.Sneaker;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Draw extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "winning_date")
    private LocalDateTime winningDate;

    @Column(name = "quantity")
    private int quantity;

    @Builder
    public Draw(
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime winningDate,
        int quantity
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.winningDate = winningDate;
        this.quantity = quantity;
    }
}