package com.platfos.pongift.validate.exception;

import com.platfos.pongift.validate.model.ParamValidate;
import org.springframework.web.bind.ServletRequestBindingException;

public class ParamValidateException extends ServletRequestBindingException {
    private ParamValidate validate;
    public ParamValidateException(ParamValidate validate){
        super(validate.toString());
        this.validate = validate;
    }

    public ParamValidate getValidate(){
        return validate;
    }
}
