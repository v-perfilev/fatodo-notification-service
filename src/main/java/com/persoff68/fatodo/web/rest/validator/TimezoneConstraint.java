package com.persoff68.fatodo.web.rest.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimezoneConstraint {
    String message() default "Invalid timezone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
