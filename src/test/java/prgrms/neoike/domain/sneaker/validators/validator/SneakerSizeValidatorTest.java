package prgrms.neoike.domain.sneaker.validators.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import prgrms.neoike.controller.dto.sneaker.request.SneakerStockRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SneakerSizeValidatorTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @DisplayName("신발 사이즈의 범위가 0 보다 작고 400 보다 크면 예외가 발생한다.")
    @ParameterizedTest(name = "사이즈 범위 테스트 {index}")
    @MethodSource("testInvalidSizeUnitsSource")
    void testInvalidSizeUnits(SneakerStockRequest request) {
        Set<ConstraintViolation<SneakerStockRequest>> validate = validator.validate(request);

        assertThat(validate.size()).isNotZero();
    }

    static List<SneakerStockRequest> testInvalidSizeUnitsSource() {
        return List.of(
            new SneakerStockRequest(123, 10),
            new SneakerStockRequest(321, 10),
            new SneakerStockRequest(412, 10),
            new SneakerStockRequest(333, 10),
            new SneakerStockRequest(101, 10),
            new SneakerStockRequest(134, 10),
            new SneakerStockRequest(111, 10)
        );
    }

    @DisplayName("신발 사이즈가 5단위가 아니라면 예외가 발생한다.")
    @ParameterizedTest(name = "사이즈 단위 테스트 {index}")
    @MethodSource("testValidSizeUnitsSource")
    void testValidSizeUnits(SneakerStockRequest request) {
        Set<ConstraintViolation<SneakerStockRequest>> validate = validator.validate(request);

        assertThat(validate).isEmpty();
    }

    static List<SneakerStockRequest> testValidSizeUnitsSource() {
        return List.of(
            new SneakerStockRequest(100, 10),
            new SneakerStockRequest(110, 10),
            new SneakerStockRequest(120, 10),
            new SneakerStockRequest(300, 10),
            new SneakerStockRequest(250, 10),
            new SneakerStockRequest(300, 10),
            new SneakerStockRequest(130, 10)
        );
    }
}