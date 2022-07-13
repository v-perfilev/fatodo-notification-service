package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.web.rest.validator.util.DateUtils;
import com.persoff68.fatodo.web.rest.vm.MonthVM;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MonthValidator implements ConstraintValidator<MonthConstraint, MonthVM> {

    @Override
    public void initialize(MonthConstraint dateParams) {
        // unimportant required method
    }

    @Override
    public boolean isValid(MonthVM vm,
                           ConstraintValidatorContext cxt) {
        if (vm == null) {
            return false;
        }
        int month = vm.getMonth();
        int year = vm.getYear();
        String timezone = vm.getTimezone();
        return DateUtils.isDateValid(month, year)
                && DateUtils.isTimezoneValid(timezone);
    }

}
