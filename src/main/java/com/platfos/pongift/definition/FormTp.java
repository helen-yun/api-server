package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품권 템플릿 유형
 * (01:판매, 02:판매취소, 03:환불, 04:유효기간연장, 05:재발송, 06:번호변경발송, 07:점유인증, 08:유효기간만료고지, 09:사용안내이미지, 10:직접발송, 11: 수기발송)
 */
@Getter
@AllArgsConstructor
public enum FormTp {
    SALES("01"),
    SALES_CANCEL("02"),
    REFUND("03"),
    EXPIRATION_EXTENSION("04"),
    RESEND("05"),
    CHANGE_NUMBER("06"),
    CERTIFICATION("07"),
    EXPIRATION("08"),
    GUIDE("09"),
    SEND_DIRECT("10"),
    SEND_GOODS("14")
    ;

    private String code;

    public static FormTp findByCode(String code){
        if(code == null) return null;

        for(FormTp giftSt : values()){
            if(code.equals(giftSt.getCode())){
                return giftSt;
            }
        }
        return null;
    }
}
