package prgrms.neoike.domain.sneaker.validators.validator;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SneakerSizeValidator implements ConstraintValidator<SneakerSize, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return isValidSizeRange(value)
            && isValidSizeUnits(value);
    }

    private boolean isValidSizeRange(int value) {
        return value >= 0 && value <= 400;
    }

    private boolean isValidSizeUnits(int value) {
        return value % 5 == 0;
    }
}
