package com.platfos.pongift.validate.validator;

import com.platfos.pongift.validate.constraints.MapConstraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Document 전용
 * 동작 안함
 */
public class MapValidator implements ConstraintValidator<MapConstraints, Object> {
    private Class<?> clazz;

    @Override
    public void initialize(MapConstraints annotation) {
        clazz = annotation.clazz();
    }

    @Override
    public boolean isValid(Object val, ConstraintValidatorContext ctx) {
        return true;
    }
}
