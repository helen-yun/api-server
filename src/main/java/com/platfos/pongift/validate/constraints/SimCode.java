package com.platfos.pongift.validate.constraints;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.validator.SimCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = SimCodeValidator.class)
@Documented
public @interface SimCode {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    SIMCodeGroup[] groupCode() default {};
}
