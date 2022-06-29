package prgrms.neoike.domain.sneaker;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Stock {

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public Stock(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantityBy(int value) {
        this.quantity += value;
    }

    public void decreaseQuantityBy(int value) {
        if (this.quantity < value) {
            throw new IllegalArgumentException(
                format("재고가 부족합니다. (현재재고: {0})", this.quantity)
            );
        }

        this.quantity -= value;
    }
}
