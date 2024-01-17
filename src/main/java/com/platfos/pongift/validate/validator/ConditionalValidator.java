package com.platfos.pongift.validate.validator;

import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.exception.advice.RestControllerExceptionAdvice;
import com.platfos.pongift.validate.constraints.Conditional;
import com.platfos.pongift.validate.constraints.SimCode;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class ConditionalValidator implements ConstraintValidator<Conditional, Object> {
    @Autowired
    SIMCodeService simCodeService;

    private String selected;
    private String value;
    private String[] targets;
    private String[] required;
    private String[] requiredGroup;

    @Override
    public void initialize(Conditional annotation) {
        selected = annotation.selected();
        value = annotation.value();
        targets = annotation.targets();
        required = annotation.required();
        requiredGroup = annotation.requiredGroup();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
        if(!ObjectUtils.isEmpty(requiredGroup)){
            boolean existValue = false;
            for (String propName : requiredGroup) {
                Object requiredValue = null;
                try { requiredValue = BeanUtils.getProperty(obj, propName); } catch (Exception e) {e.printStackTrace();}
                if(!ObjectUtils.isEmpty(requiredValue)){
                    existValue = true;
                }
            }

            if(existValue){
                for (String propName : requiredGroup) {
                    Object requiredValue = null;
                    try { requiredValue = BeanUtils.getProperty(obj, propName); } catch (Exception e) {e.printStackTrace();}
                    if(ObjectUtils.isEmpty(requiredValue)){
                        return validFail(RestControllerExceptionAdvice.CONSTRAINT_REQUIRE_MESSAGE, propName, ctx);
                    }
                }
            }
        }

        if(StringUtils.isEmpty(selected)) return true;

        Class<?> clzz = obj.getClass();
        Object val = null;
        try { val = BeanUtils.getProperty(obj, selected); } catch (Exception e) {e.printStackTrace();}
        if(ObjectUtils.isEmpty(val)) return true;

        Field selectedField = null;
        try { selectedField = clzz.getDeclaredField(selected); } catch (Exception e) {e.printStackTrace();}
        if(ObjectUtils.isEmpty(selectedField)) return true;

        for (String propName : targets) {
            Field propField = null;
            try { propField = clzz.getDeclaredField(propName); } catch (Exception e) {e.printStackTrace();}
            if(ObjectUtils.isEmpty(propField)) return true;

            Object targetValue = null;
            try { targetValue = BeanUtils.getProperty(obj, propName); } catch (Exception e) {e.printStackTrace();}

            if(StringUtils.isEmpty(value) & ObjectUtils.isEmpty(targetValue)){
                return validFail(RestControllerExceptionAdvice.CONSTRAINT_REQUIRE_MESSAGE, propName, ctx);
            }else if(!StringUtils.isEmpty(value) && (value.equals(val) & ObjectUtils.isEmpty(targetValue))){
                return validFail(RestControllerExceptionAdvice.CONSTRAINT_REQUIRE_MESSAGE, propName, ctx);
            }else if(!StringUtils.isEmpty(value) && !value.equals(val)){
                return true;
            }

            if(selectedField.isAnnotationPresent(SimCode.class) & propField.isAnnotationPresent(SimCode.class)){
                SIMCodeGroup selectedSimCodeGroup = selectedField.getAnnotation(SimCode.class).groupCode()[0];
                SIMCode groupCode = simCodeService.getSIMCode(selectedSimCodeGroup, (String) val);
                if(groupCode == null) {
                    String groupCodeName = simCodeService.getSIMCodeGroupName(selectedSimCodeGroup);
                    if(StringUtils.isEmpty(groupCodeName)) return validFail("Illegal Code Value", selected, ctx);
                    else return validFail("Illegal Code Value : " + groupCodeName, selected, ctx);
                }

                SIMCodeGroup simCodeGroup = SIMCodeGroup.findByParentCodeGroup(selectedSimCodeGroup, (String) val);
                SIMCode code = simCodeService.getSIMCode(simCodeGroup, (String) targetValue);
                if(code == null) {
                    String groupCodeName = simCodeService.getSIMCodeGroupName(simCodeGroup);
                    if(StringUtils.isEmpty(groupCodeName)) return validFail("Illegal Code Value", propName, ctx);
                    else return validFail("Illegal Code Value : " + groupCodeName, propName, ctx);
                }
            }
        }

        if(value.equals(val) & !ObjectUtils.isEmpty(required)){
            if(selected.equals("timeTp")) System.out.println("????");
            for (String propName : required) {
                Object requiredValue = null;
                try { requiredValue = BeanUtils.getProperty(obj, propName); } catch (Exception e) {e.printStackTrace();}
                if(ObjectUtils.isEmpty(requiredValue)){
                    return validFail(RestControllerExceptionAdvice.CONSTRAINT_REQUIRE_MESSAGE, propName, ctx);
                }
            }
        }

        return true;
    }

    private boolean validFail(String message, String fieldName, ConstraintValidatorContext ctx){
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addPropertyNode(fieldName).addConstraintViolation();
        return false;
    }

}
