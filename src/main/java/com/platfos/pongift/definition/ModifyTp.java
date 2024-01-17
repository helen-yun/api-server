package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품권 원장 변경 유형
 * (01:판매, 02:판매취소, 03:환불, 04:사용, 05:사용취소, 06:재발송, 07:번호변경발송, 08:유효기간연장, 09:선물하기)
 */
@Getter
@AllArgsConstructor
public enum ModifyTp {
    SALES("01"),
    SALES_CANCEL("02"),
    REFUND("03"),
    USE("04"),
    USE_CANCEL("05"),
    RESEND("06"),
    CHANGE_NUMBER("07"),
    EXPIRATION_EXTENSION("08"),
    GIFT("09"),
    SEND_DIRECT("10");

    private String code;

    public static ModifyTp findByCode(String code) {
        if (code == null) return null;

        for (ModifyTp modifyTp : values()) {
            if (code.equals(modifyTp.getCode())) {
                return modifyTp;
            }
        }
        return null;
    }
}
