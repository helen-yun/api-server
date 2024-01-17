package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 시스템 모드
 */
@Getter
@AllArgsConstructor
public enum SystemMode {
    PRODUCT("01"),
    SANDBOX("02");

    private String code;

    public static SystemMode findByCode(String code){
        if(code == null) return null;

        for(SystemMode systemMode : values()){
            if(code.equals(systemMode.getCode())){
                return systemMode;
            }
        }
        return null;
    }
}
