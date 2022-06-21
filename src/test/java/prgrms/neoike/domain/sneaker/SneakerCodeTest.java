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
import static prgrms.neoike.domain.sneaker.SneakerCategory.*;
import static prgrms.neoike.domain.sneaker.manager.SneakerCodeCreator.createSneakerCode;

class SneakerCodeTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("신발 코드를 생성할 수 있다.")
    @ParameterizedTest(name = "신발 코드 {index}")
    @MethodSource("testCreateSneakerCodeSource")
    void testCreateSneakerCode(String code) {
        assertThat(code).isNotNull().hasSize(10);
        assertThat(code.substring(0, 2)).isUpperCase();
        assertThat(code).contains("-");
        assertThat(isDigits(code.substring(3))).isTrue();
    }

    static List<String> testCreateSneakerCodeSource() {
        return List.of(
            createSneakerCode(RUNNING),
            createSneakerCode(LIFESTYLE),
            createSneakerCode(BASKETBALL),
            createSneakerCode(SANDAL),
            createSneakerCode(JORDAN),
            createSneakerCode(SOCCER),
            createSneakerCode(RUNNING),
            createSneakerCode(LIFESTYLE),
            createSneakerCode(BASKETBALL),
            createSneakerCode(SANDAL),
            createSneakerCode(JORDAN),
            createSneakerCode(SOCCER)
        );
    }

    @DisplayName("신발 코드 형식에 맞지 않은 코드가 입력되면 예외가 발생한다.")
    @ParameterizedTest(name = "잘못된 신발 코드 {index}")
    @MethodSource("testInvalidSneakerCodeSource")
    void testInvalidSneakerCode(String sneakerCode) {
        Set<ConstraintViolation<String>> validate = validator.validate(sneakerCode);

        assertThat(validate.size()).isNotZero();
    }

    static List<String> testInvalidSneakerCodeSource() {
        return List.of(
            randomAlphabetic(11),
            randomAlphabetic(9).toUpperCase(),
            randomAlphanumeric(3).toUpperCase() + randomNumeric(7),
            randomAlphanumeric(1) + "-" + randomNumeric(7),
            randomAlphanumeric(2) + randomNumeric(8),
            randomAlphanumeric(3) + "-" + randomNumeric(4),
            randomAscii(3) + randomNumeric(7),
            randomAlphanumeric(1) + "--" + randomNumeric(7),
            randomAlphanumeric(4) + randomNumeric(7),
            randomAlphanumeric(5) + "    " + randomNumeric(1),
            randomAlphanumeric(2) + randomNumeric(4)
        );
    }
}