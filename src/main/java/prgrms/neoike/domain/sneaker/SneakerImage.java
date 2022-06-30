package prgrms.neoike.domain.sneaker;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import prgrms.neoike.domain.BaseTimeEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Getter
@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "sneaker_image_id_path",
            columnNames = {"sneaker_image_id", "path"}
        )
    }
)
@NoArgsConstructor(access = PROTECTED)
public class SneakerImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "sneaker_image_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "path", length = 150, nullable = false)
    private String path;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sneaker_id")
    private Sneaker sneaker;

    public SneakerImage(String path) {
        this.path = path;
    }

    public void setSneaker(Sneaker sneaker) {
        this.sneaker = sneaker;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("path", path)
            .toString();
    }
}
