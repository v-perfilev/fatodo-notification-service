package com.persoff68.fatodo.web.rest.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MonthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MonthConstraint {
    String message() default "Invalid month";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
