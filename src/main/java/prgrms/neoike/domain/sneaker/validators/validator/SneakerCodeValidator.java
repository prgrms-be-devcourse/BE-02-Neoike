package prgrms.neoike.domain.sneaker.validators.validator;

import prgrms.neoike.domain.sneaker.validators.annotation.SneakerCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isAlpha;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;

public class SneakerCodeValidator implements ConstraintValidator<SneakerCode, String> {

    private int length;

    @Override
    public void initialize(SneakerCode constraintAnnotation) {
        length = constraintAnnotation.length();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isSameWithLength(value)
            && validateCodePrefix(value)
            && isContainsHyphen(value)
            && isDigits(value.substring(3));
    }

    private boolean isSameWithLength(String value) {
        if (value.isBlank()) {
            return true;
        }

        return value.length() == length;
    }

    private boolean validateCodePrefix(String value) {
        String prefix = value.substring(0, 2);

        return isAllUpperCase(prefix) && isAlpha(prefix);
    }

    private boolean isContainsHyphen(String value) {
        return value.contains("-");
    }
}
