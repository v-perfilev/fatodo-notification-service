package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.web.rest.validator.util.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MonthValidator implements ConstraintValidator<MonthConstraint, Integer> {

    @Override
    public void initialize(MonthConstraint dateParams) {
        // unimportant required method
    }

    @Override
    public boolean isValid(Integer input,
                           ConstraintValidatorContext cxt) {
        if (input == null) {
            return false;
        }
        return DateUtils.isMonthValid(input);
    }

}
