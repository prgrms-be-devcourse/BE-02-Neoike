package prgrms.neoike.domain.sneaker;


import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.Valid;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class SneakerImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "sneaker_image_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Valid
    @Embedded
    private Image image;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_id", nullable = false)
    private Sneaker sneaker;

    public SneakerImage(Image image) {
        this.image = image;
    }

    public void uploadSneakerImage(Sneaker sneaker) {
        this.sneaker = sneaker;
        sneaker.getSneakerImages().add(this);
    }
}
