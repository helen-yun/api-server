package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 전시 상태 - GF200301000141
 * (01:판매대기, 02:전시판매, 03:판매중지)
 */
@Getter
@AllArgsConstructor
public enum ExhibitSt {
    WAITING_FOR_SALES("01"),
    EXHIBITION_SALE("02"),
    STOP_SELLING("03"),
    SOLD_OUT("04"),
    ;

    private String code;

    public static ExhibitSt findByCode(String code){
        if(code == null) return null;

        for(ExhibitSt exhibitSt : values()){
            if(code.equals(exhibitSt.getCode())){
                return exhibitSt;
            }
        }
        return null;
    }
}
