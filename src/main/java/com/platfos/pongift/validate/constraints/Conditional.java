package com.platfos.pongift.validate.constraints;

import com.platfos.pongift.validate.validator.ConditionalValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(Conditionals.class)
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ConditionalValidator.class)
public @interface Conditional {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String selected() default "";
    String value() default "";
    String[] targets() default "";
    String[] requiredGroup() default {};
    String[] required() default {};
}
