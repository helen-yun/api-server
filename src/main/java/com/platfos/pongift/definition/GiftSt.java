package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품권 상태
 * (01:미사용, 02:사용, 03:정지)
 */
@Getter
@AllArgsConstructor
public enum GiftSt {
    UNUSED("01"),
    USED("02"),
    STOP("03");

    private String code;

    public static GiftSt findByCode(String code){
        if(code == null) return null;

        for(GiftSt giftSt : values()){
            if(code.equals(giftSt.getCode())){
                return giftSt;
            }
        }
        return null;
    }
}
