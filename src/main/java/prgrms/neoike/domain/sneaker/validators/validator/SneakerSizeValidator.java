package prgrms.neoike.domain.sneaker.validators.validator;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SneakerSizeValidator implements ConstraintValidator<SneakerSize, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return validateSizeRange(value)
            && validateSizeUnits(value);
    }

    private boolean validateSizeRange(int value) {
        return value >= 0 && value <= 400;
    }

    private boolean validateSizeUnits(int value) {
        return value % 5 == 0;
    }
}
