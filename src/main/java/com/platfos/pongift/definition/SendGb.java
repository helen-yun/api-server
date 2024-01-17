package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 문자메시지 발송 구분
 * (01:판매, 02:판매취소, 03:환불, 04:유효기간연장, 05:재발송, 06:번호변경발송, 07:만료일안내)
 */
@Getter
@AllArgsConstructor
public enum SendGb {
    SALES("01"),
    SALES_CANCEL("02"),
    REFUND("03"),
    EXPIRATION_EXTENSION("04"),
    RESEND("05"),
    CHANGE_NUMBER("06"),
    EXPIRATION("07"),
    SEND_DIRECT("08");

    private String code;

    public static SendGb findByCode(String code) {
        if (code == null) return null;

        for (SendGb modifyTp : values()) {
            if (code.equals(modifyTp.getCode())) {
                return modifyTp;
            }
        }
        return null;
    }
}
