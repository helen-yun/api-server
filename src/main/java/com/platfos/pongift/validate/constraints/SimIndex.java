package com.platfos.pongift.validate.constraints;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.validator.SimIndexValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = SimIndexValidator.class)
@Documented
public @interface SimIndex {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    SIMIndex[] simIndex() default {};
}