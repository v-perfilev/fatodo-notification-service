package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.web.rest.validator.util.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimezoneValidator implements ConstraintValidator<TimezoneConstraint, String> {

    @Override
    public void initialize(TimezoneConstraint dateParams) {
        // unimportant required method
    }

    @Override
    public boolean isValid(String input,
                           ConstraintValidatorContext cxt) {
        if (input == null) {
            return false;
        }
        return DateUtils.isTimezoneValid(input);
    }

}
