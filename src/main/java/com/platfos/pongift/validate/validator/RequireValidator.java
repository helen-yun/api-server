package com.platfos.pongift.validate.validator;

import com.platfos.pongift.validate.constraints.Require;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequireValidator implements ConstraintValidator<Require, Object> {
    @Override
    public boolean isValid(Object val, ConstraintValidatorContext context) {
        return (!ObjectUtils.isEmpty(val));
    }
}
