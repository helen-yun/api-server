package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 서비스 상태
 * (01:대기, 02:사용, 03:종료)
 */
@Getter
@AllArgsConstructor
public enum MIMServiceSt {
    WAITTING("01"),
    ACTIVE("02"),
    TERMINATION("03");

    private String code;


    public static MIMServiceSt findByCode(String code){
        if(code == null) return null;

        for(MIMServiceSt serviceSt : values()){
            if(code.equals(serviceSt.getCode())){
                return serviceSt;
            }
        }
        return null;
    }
}
