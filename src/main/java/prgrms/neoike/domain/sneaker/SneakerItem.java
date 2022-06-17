package prgrms.neoike.domain.sneaker;

import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.draw.Draw;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SneakerItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "draw_id")
    private Draw draw;

    @ManyToOne
    @JoinColumn(name = "sneaker_stock_id")
    private SneakerStock sneakerStock;

    @Builder
    public SneakerItem(int quantity, Draw draw, SneakerStock sneakerStock) {
        this.quantity = quantity;
        this.draw = draw;
        this.sneakerStock = sneakerStock;
    }
}