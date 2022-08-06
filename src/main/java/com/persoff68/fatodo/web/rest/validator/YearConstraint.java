package com.persoff68.fatodo.web.rest.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YearValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearConstraint {
    String message() default "Invalid year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
