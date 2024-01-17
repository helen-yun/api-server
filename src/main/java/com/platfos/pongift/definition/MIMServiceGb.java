package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 서비스 구분
 * (01:offline, 02:online, 03:mobile)
 */
@Getter
@AllArgsConstructor
public enum MIMServiceGb {
    OFFLINE("01"),
    ONLINE("02"),
    MOBILE("03");

    private String code;


    public static MIMServiceGb findByCode(String code){
        if(code == null) return null;

        for(MIMServiceGb serviceGb : values()){
            if(code.equals(serviceGb.getCode())){
                return serviceGb;
            }
        }
        return null;
    }
}
