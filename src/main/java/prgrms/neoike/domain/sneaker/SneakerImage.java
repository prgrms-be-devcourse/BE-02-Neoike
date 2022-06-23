package prgrms.neoike.domain.sneaker;


import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "sneaker_image_id_path",
            columnNames = {"sneaker_image_id", "path"}
        )
    }
)
public class SneakerImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "sneaker_image_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "path", nullable = false)
    String path;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_id")
    private Sneaker sneaker;

    public SneakerImage(String path) {
        this.path = path;
    }

    public void uploadSneakerImage(Sneaker sneaker) {
        this.sneaker = sneaker;
        sneaker.getSneakerImages().add(this);
    }
}
