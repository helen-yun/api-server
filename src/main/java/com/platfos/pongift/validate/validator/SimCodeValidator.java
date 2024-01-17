package com.platfos.pongift.validate.validator;

import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.constraints.SimCode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class SimCodeValidator implements ConstraintValidator<SimCode, Object> {
    @Autowired
    SIMCodeService simCodeService;

    private SIMCodeGroup[] groupCode;

    @Override
    public void initialize(SimCode annotation) {
        groupCode = annotation.groupCode();
    }

    @Override
    public boolean isValid(Object val, ConstraintValidatorContext ctx) {
        if(!ObjectUtils.isEmpty(val)){
            if(!((val instanceof String) | (val instanceof List))){
                return validFail("Illegal Code Value", ctx);
            }

            List<String> values = new ArrayList<>();
            if(val instanceof String){
                values.add((String) val);
            }else if(val instanceof List){
                values.addAll((List<? extends String>) val);
            }

            if(!ObjectUtils.isEmpty(groupCode)){
                List<SIMCode> codes = simCodeService.getSIMCodeList(groupCode[0], values);
                if(codes == null | (codes != null && (codes.size() == 0 | (codes.size() != values.size())))){
                    String groupCodeName = simCodeService.getSIMCodeGroupName(groupCode[0]);

                    if(StringUtils.isEmpty(groupCodeName)) return validFail("Illegal Code Value", ctx);
                    else return validFail("Illegal Code Value : " + groupCodeName, ctx);
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
