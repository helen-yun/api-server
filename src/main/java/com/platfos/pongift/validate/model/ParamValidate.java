package com.platfos.pongift.validate.model;

import com.platfos.pongift.definition.ResponseCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParamValidate {
    private ResponseCode code;
    private String filedName;
    private Object rejectValue;
    private String causeMessage;

    public boolean hasError(){
        return (code != ResponseCode.SUCCESS);
    }

    public static ParamValidate buildSuccess(){
        return build(ResponseCode.SUCCESS);
    }
    public static ParamValidate build(ResponseCode code){
        return new ParamValidate(code, null, null, null);
    }
    public static ParamValidate build(ResponseCode code, String filedName){
        return new ParamValidate(code, filedName, null, null);
    }
    public static ParamValidate build(ResponseCode code, String filedName, Object rejectValue){
        return new ParamValidate(code, filedName, rejectValue, null);
    }
    public static ParamValidate build(ResponseCode code, String filedName, Object rejectValue, String causeMessage){
        return new ParamValidate(code, filedName, rejectValue, causeMessage);
    }

    @Override
    public String toString() {
        return "ParamValidate {" +
                "code=" + code +
                ", filedName='" + filedName + '\'' +
                ", rejectValue=" + rejectValue +
                ", causeMessage='" + causeMessage + '\'' +
                '}';
    }
}
