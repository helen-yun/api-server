package com.platfos.pongift.validate.validator;

import com.platfos.pongift.validate.constraints.PhoneNumber;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private final String pattern1 = "^01(?:0|1|[6-9])";
    private final String pattern2 = "(?:\\d{3}|\\d{4})";
    private final String pattern3 = "\\d{4}$";

    private final String pattern4 = "^(?:[+](\\d(.+))|[+](\\d(.+) \\d(.+)))-1(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";

    private boolean international;
    private String separator;

    @Override
    public void initialize(PhoneNumber annotation) {
        international = annotation.international();
        separator = annotation.separator();
    }

    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if(!StringUtils.isEmpty(val)){
            if(international){
                if(!Pattern.matches(pattern4, val)){
                    return validFail("Cell phone number format is incorrect (+dd-dd-ddd(d)-dddd)", ctx);
                }
            }else{
                if(!Pattern.matches(pattern1+separator+pattern2+separator+pattern3, val)){
                    return validFail("Cell phone number format is incorrect (01x"+separator+"xxx(x)"+separator+"xxxx)", ctx);
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
