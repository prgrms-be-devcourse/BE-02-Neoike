package prgrms.neoike.domain.sneaker.validators.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;
import static org.assertj.core.api.Assertions.assertThat;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

class SneakerCodeValidatorTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("신발 코드를 생성할 수 있다.")
    @RepeatedTest(value = 10, name = "랜덤 신발 코드 {currentRepetition} of {totalRepetitions}")
    void testCreateSneakerCode() {
        String code = createSneakerCode();

        assertThat(code).isNotNull().hasSize(10);
        assertThat(code.substring(0, 2)).isUpperCase();
        assertThat(code).contains("-");
        assertThat(isDigits(code.substring(3))).isTrue();
    }

    @DisplayName("신발 코드 형식에 맞지 않은 코드가 입력되면 예외가 발생한다.")
    @ParameterizedTest(name = "잘못된 신발 코드 {index}")
    @MethodSource("testInvalidSneakerCodeSource")
    void testInvalidSneakerCode(String sneakerCode) {
        Set<ConstraintViolation<SneakerRequest>> validate = validator
            .validate(
                new SneakerRequest(
                    MEN, JORDAN, "jordan", 10000, "description", sneakerCode, now()
                )
            );

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

    public static String createSneakerCode() {
        String prefix = randomAlphabetic(2).toUpperCase();
        String postfix = randomNumeric(7);

        return format("{0}-{1}", prefix, postfix);
    }
}