package prgrms.neoike.domain.sneaker;

import lombok.Getter;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.sneaker.validators.annotation.CodeCheck;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class SneakerCode {

    @CodeCheck(length = 10, message = "상품 코드의 형식이 잘 못 되었습니다. (입력값: ${validatedValue})")
    @Column(name = "code", length = 10, nullable = false, updatable = false)
    private String code;

    public SneakerCode(String code) {
        this.code = code;
    }
}
