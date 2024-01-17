package com.platfos.pongift.validate.constraints;

import com.platfos.pongift.validate.validator.CustomCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = CustomCodeValidator.class)
@Documented
public @interface CustomCode {
    String message() default "Illegal Index Code Value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] codes() default {};
}
