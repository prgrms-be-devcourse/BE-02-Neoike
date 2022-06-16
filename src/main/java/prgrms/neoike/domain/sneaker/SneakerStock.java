package prgrms.neoike.domain.sneaker;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SneakerStock extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_id")
    private Sneaker sneaker;

    @Enumerated
    @Column(name = "size", nullable = false)
    private Size size;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Builder
    public SneakerStock(Sneaker sneaker, Size size, int quantity) {
        this.sneaker = sneaker;
        this.size = size;
        this.quantity = quantity;
    }
}