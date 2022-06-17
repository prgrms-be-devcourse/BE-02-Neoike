package prgrms.neoike.domain.sneaker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import prgrms.neoike.domain.sneaker.validators.annotation.CodeCheck;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class SneakerCode {

    @CodeCheck(size = 10, message = "상품 코드의 형식이 잘 못 되었습니다. (입력값: ${validatedValue})")
    @Column(name = "sneaker_code", length = 10, nullable = false, updatable = false)
    private String code;

    public SneakerCode(Category category) {
        this.code = createCode(category);
    }

    private String createCode(Category category) {
        String preFix = category.name().substring(0, 2);
        String postFix = RandomStringUtils.randomNumeric(7);

        return format("{0}-{1}", preFix, postFix);
    }
}
