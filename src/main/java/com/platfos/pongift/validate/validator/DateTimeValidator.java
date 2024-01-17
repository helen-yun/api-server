package com.platfos.pongift.validate.validator;

import com.platfos.pongift.validate.constraints.DateTime;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeValidator implements ConstraintValidator<DateTime, String> {
    private String format;

    @Override
    public void initialize(DateTime annotation) {
        format = annotation.format();
    }

    @Override
    public boolean isValid(String val, ConstraintValidatorContext ctx) {
        if(!StringUtils.isEmpty(val)){
            try{
                SimpleDateFormat dateFormat = new  SimpleDateFormat(format);
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(val);
            }catch (ParseException e){
                return false;
            }
        }
        return true;
    }
}
