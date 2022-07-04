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

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException(
                format("재고가 부족합니다. (현재재고: {0})", this.quantity)
            );
        }

        this.quantity -= quantity;
    }
}
