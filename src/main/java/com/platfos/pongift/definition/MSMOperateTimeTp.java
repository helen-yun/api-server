package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 매장 운영 시간 유형
 * (01:24시간, 02:휴무일)
 */
@Getter
@AllArgsConstructor
public enum MSMOperateTimeTp {
    WORKING("01"),
    CLOSED("02");

    private String code;


    public static MSMOperateTimeTp findByCode(String code){
        if(code == null) return null;

        for(MSMOperateTimeTp timeTp : values()){
            if(code.equals(timeTp.getCode())){
                return timeTp;
            }
        }
        return null;
    }
}
