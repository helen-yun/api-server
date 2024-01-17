package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 문자 전송 발송 상태
 * (01:대기, 02:성공, 03:실패)
 */
@Getter
@AllArgsConstructor
public enum SIMTransmitInfoSendSt {
    READY("01"),
    SUCCESS("02"),
    FAIL("03");

    private String code;

    public static SIMTransmitInfoSendSt findByCode(String code){
        if(code == null) return null;

        for(SIMTransmitInfoSendSt sendSt : values()){
            if(code.equals(sendSt.getCode())){
                return sendSt;
            }
        }
        return null;
    }
}
