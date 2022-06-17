package prgrms.neoike.domain.sneaker.validators.validator;

import prgrms.neoike.domain.sneaker.validators.annotation.CodeCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static io.micrometer.core.instrument.util.StringUtils.isNotBlank;

public class CodeCheckValidator implements ConstraintValidator<CodeCheck, String> {

    private int size;

    @Override
    public void initialize(CodeCheck constraintAnnotation) {
        size = constraintAnnotation.size();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isNotBlank(value) && value.length() == size && value.contains("-");
    }
}
