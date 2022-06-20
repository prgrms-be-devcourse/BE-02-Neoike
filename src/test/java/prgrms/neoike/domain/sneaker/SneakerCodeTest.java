package prgrms.neoike.domain.sneaker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;
import static org.assertj.core.api.Assertions.assertThat;
import static prgrms.neoike.domain.sneaker.Category.*;
import static prgrms.neoike.domain.sneaker.SneakerCodeCreator.createSneakerCode;

class SneakerCodeTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("신발 코드를 생성할 수 있다.")
    @ParameterizedTest(name = "신발 코드 {index}")
    @MethodSource("testCreateSneakerCodeSource")
    void testCreateSneakerCode(SneakerCode sneakerCode) {
        String code = sneakerCode.getCode();

        assertThat(code).isNotNull().hasSize(10);
        assertThat(code.substring(0, 2)).isUpperCase();
        assertThat(code).contains("-");
        assertThat(isDigits(code.substring(3))).isTrue();
    }

    static List<SneakerCode> testCreateSneakerCodeSource() {
        return List.of(
            new SneakerCode(createSneakerCode(RUNNING)),
            new SneakerCode(createSneakerCode(LIFESTYLE)),
            new SneakerCode(createSneakerCode(BASKETBALL)),
            new SneakerCode(createSneakerCode(SANDAL)),
            new SneakerCode(createSneakerCode(JORDAN)),
            new SneakerCode(createSneakerCode(SOCCER)),
            new SneakerCode(createSneakerCode(RUNNING)),
            new SneakerCode(createSneakerCode(LIFESTYLE)),
            new SneakerCode(createSneakerCode(BASKETBALL)),
            new SneakerCode(createSneakerCode(SANDAL)),
            new SneakerCode(createSneakerCode(JORDAN)),
            new SneakerCode(createSneakerCode(SOCCER))
        );
    }

    @DisplayName("신발 코드 형식에 맞지 않은 코드가 입력되면 예외가 발생한다.")
    @ParameterizedTest(name = "잘못된 신발 코드 {index}")
    @MethodSource("testInvalidSneakerCodeSource")
    void testInvalidSneakerCode(SneakerCode sneakerCode) {
        Set<ConstraintViolation<SneakerCode>> validate = validator.validate(sneakerCode);

        assertThat(validate.size()).isNotZero();
    }

    static List<SneakerCode> testInvalidSneakerCodeSource() {
        return List.of(
            new SneakerCode(null),
            new SneakerCode(randomAlphabetic(11)),
            new SneakerCode(randomAlphabetic(9).toUpperCase()),
            new SneakerCode(randomAlphanumeric(3).toUpperCase() + randomNumeric(7)),
            new SneakerCode(randomAlphanumeric(1) + "-" + randomNumeric(7)),
            new SneakerCode(randomAlphanumeric(2) + randomNumeric(8)),
            new SneakerCode(randomAlphanumeric(3) + "-" + randomNumeric(4)),
            new SneakerCode(randomAscii(3) + randomNumeric(7)),
            new SneakerCode(randomAlphanumeric(1) + "--" + randomNumeric(7)),
            new SneakerCode(randomAlphanumeric(4) + randomNumeric(7)),
            new SneakerCode(randomAlphanumeric(5) + "    " + randomNumeric(1)),
            new SneakerCode(randomAlphanumeric(2) + randomNumeric(4))
        );
    }
}