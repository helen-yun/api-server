package com.platfos.pongift.validate.validator;

import com.platfos.pongift.validate.constraints.Number;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class NumberValidator implements ConstraintValidator<Number, String> {
    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if(!StringUtils.isEmpty(val)){
            if(!Pattern.matches("^[0-9]*$", val)){
                return false;
            }
        }
        return true;
    }
}
