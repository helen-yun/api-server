package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 서비스 형태 1
 * (01:프렌차이즈, 02:Local)
 */
@Getter
@AllArgsConstructor
public enum MIMServiceForm {
    FRANCHISE("01"),
    LOCAL("02");

    private String code;


    public static MIMServiceForm findByCode(String code){
        if(code == null) return null;

        for(MIMServiceForm serviceForm : values()){
            if(code.equals(serviceForm.getCode())){
                return serviceForm;
            }
        }
        return null;
    }
}
