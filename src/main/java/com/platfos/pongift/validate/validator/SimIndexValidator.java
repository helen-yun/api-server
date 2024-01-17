package com.platfos.pongift.validate.validator;

import com.platfos.pongift.data.sim.service.SIMDynamicService;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.constraints.SimIndex;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SimIndexValidator implements ConstraintValidator<SimIndex, String> {
    @Autowired
    SIMDynamicService simDynamicService;

    private SIMIndex[] simIndex;

    @Override
    public void initialize(SimIndex annotation) {
        simIndex = annotation.simIndex();
    }

    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if(!StringUtils.isEmpty(val)){
            if(!ObjectUtils.isEmpty(simIndex)){
                if(!val.startsWith(simIndex[0].getCode())){
                    return validFail("Illegal Index Code Value", ctx);
                }
                if(val.length() != 14){
                    return validFail("Illegal Index Code Value", ctx);
                }

                if(!simDynamicService.check(simIndex[0], val)){ //존재 여부
                    return validFail("Illegal Index Code Value", ctx);
                }
            }
        }
        return true;
    }

    private boolean validFail(String message, ConstraintValidatorContext ctx){
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }

}
