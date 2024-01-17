package com.platfos.pongift.validate.validator;

import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.validate.constraints.CustomCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomCodeValidator implements ConstraintValidator<CustomCode, String> {
    @Autowired
    SIMCodeService simCodeService;

    private String[] codes;

    @Override
    public void initialize(CustomCode annotation) {
        codes = annotation.codes();
    }

    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if(!StringUtils.isEmpty(val)){
            boolean checkCustomCode = false;
            for(String code : codes){
                if(code.equals(val)){
                    checkCustomCode = true;
                    break;
                }
            }
            if(!checkCustomCode){
                return false;
            }
        }
        return true;
    }
}
