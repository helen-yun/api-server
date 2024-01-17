package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품권 유형
 * (01:교환권, 02:금액형(1회), 03:금액형(잔액), 04:금액형(충전)
 */
@Getter
@AllArgsConstructor
public enum GiftCardTp {
    EXCHANGE("01"),
    PRICE_DISPOSABLE("02"),
    PRICE_BALANCE("03"),
    PRICE_CHARGE("04");

    private String code;

    public static GiftCardTp findByCode(String code){
        if(code == null) return null;

        for(GiftCardTp giftCardTp : values()){
            if(code.equals(giftCardTp.getCode())){
                return giftCardTp;
            }
        }
        return null;
    }
}
