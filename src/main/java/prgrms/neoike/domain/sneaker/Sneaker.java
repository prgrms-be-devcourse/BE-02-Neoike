package prgrms.neoike.domain.sneaker;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Sneaker extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "code", length = 10)
    private String code;

    @Enumerated(value = STRING)
    private Category category;

    @Column(name = "price")
    private int price;

    @Column(name = "description")
    private String description;

    @Enumerated(value = STRING)
    @Column(name = "member_type", length = 10)
    private MemberType memberType;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Builder
    public Sneaker(
        String name,
        String code,
        Category category,
        int price,
        String description,
        MemberType memberType,
        LocalDateTime releaseDate
    ) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.price = price;
        this.description = description;
        this.memberType = memberType;
        this.releaseDate = releaseDate;
    }
}