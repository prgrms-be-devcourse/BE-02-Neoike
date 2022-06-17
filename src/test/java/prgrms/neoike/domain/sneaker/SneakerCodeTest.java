package prgrms.neoike.domain.sneaker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static prgrms.neoike.domain.sneaker.Category.*;

class SneakerCodeTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("신발 코드를 생성할 수 있다.")
    @ParameterizedTest(name = "신발 코드 {index}")
    @MethodSource("testCreateSneakerCodeSource")
    void testCreateSneakerCode(SneakerCode sneakerCode) {
        Set<ConstraintViolation<SneakerCode>> validate = validator.validate(sneakerCode);
        assertThat(validate).isEmpty();

        String code = sneakerCode.getCode();

        assertThat(code).isNotNull().contains("-").hasSize(10);
    }

    static List<SneakerCode> testCreateSneakerCodeSource() {
        return List.of(
            new SneakerCode(RUNNING),
            new SneakerCode(SOCCER),
            new SneakerCode(BASKETBALL),
            new SneakerCode(LIFESTYLE),
            new SneakerCode(SANDAL),
            new SneakerCode(JORDAN),
            new SneakerCode(RUNNING),
            new SneakerCode(SOCCER),
            new SneakerCode(BASKETBALL),
            new SneakerCode(LIFESTYLE),
            new SneakerCode(SANDAL),
            new SneakerCode(JORDAN)
        );
    }
}