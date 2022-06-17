package prgrms.neoike.domain.sneaker.validators.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import prgrms.neoike.domain.sneaker.validators.annotation.CodeCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static io.micrometer.core.instrument.util.StringUtils.isNotBlank;

public class CodeCheckValidator implements ConstraintValidator<CodeCheck, String> {

    private int length;

    @Override
    public void initialize(CodeCheck constraintAnnotation) {
        length = constraintAnnotation.length();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isNotBlank(value)
            && value.length() == length
            && StringUtils.isAllUpperCase(value.substring(0, 2))
            && value.contains("-")
            && NumberUtils.isDigits(value.substring(3));
    }
}
