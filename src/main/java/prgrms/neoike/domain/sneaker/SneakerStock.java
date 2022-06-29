package prgrms.neoike.domain.sneaker;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table
@NoArgsConstructor(access = PROTECTED)
public class SneakerStock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "sneaker_stock_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "size", nullable = false)
    private int size;

    @Embedded
    private Stock stock;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_id", nullable = false)
    private Sneaker sneaker;

    @Builder
    public SneakerStock(int size, Stock stock) {
        this.size = size;
        this.stock = stock;
    }

    public void setSneaker(Sneaker sneaker) {
        this.sneaker = sneaker;
    }

    public void addQuantity(int quantity) {
        this.stock.increaseQuantityBy(quantity);
    }
}