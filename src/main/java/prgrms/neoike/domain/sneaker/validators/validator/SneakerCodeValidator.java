package prgrms.neoike.domain.sneaker.validators.validator;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;

public class SneakerCodeValidator implements ConstraintValidator<SneakerCode, String> {

    private int length;

    @Override
    public void initialize(SneakerCode constraintAnnotation) {
        length = constraintAnnotation.length();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isNotBlank(value)
            && isSameWithLength(value)
            && isValidPrefix(value)
            && isContainsHyphen(value)
            && isValidPostfix(value);
    }

    private boolean isSameWithLength(String value) {
        return value.length() == length;
    }

    private boolean isValidPrefix(String value) {
        String prefix = value.substring(0, 2);

        return isAllUpperCase(prefix) && isAlpha(prefix);
    }

    private boolean isContainsHyphen(String value) {
        return value.contains("-");
    }

    private boolean isValidPostfix(String value) {
        return isDigits(value.substring(3));
    }
}
