package com.platfos.pongift.validate.constraints;

import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.validate.validator.FileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = FileValidator.class)
@Documented
public @interface File {
    String message() default "UNSUPPORTED FILE FORMAT : supported format -> {supportFileFormat}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String maxFileSize() default "";
    int minImageWidth() default Integer.MIN_VALUE;
    int minImageHeight() default Integer.MIN_VALUE;
    int maxImageWidth() default Integer.MAX_VALUE;
    int maxImageHeight() default Integer.MAX_VALUE;
    String[] supportFileFormat() default {};
    AttachDataType[] supportAttachType() default {};
}
