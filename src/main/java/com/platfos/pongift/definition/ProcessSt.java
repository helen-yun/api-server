package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 채널 서비스(제어) 처리 상태
 * (01:대기, 02:실행중, 03:실행완료, 04:에러발생, 05:미지원채널)
 */
@Getter
@AllArgsConstructor
public enum ProcessSt {
    READY("01"),
    WORKING("02"),
    COMPLETED("03"),
    ERROR("04"),
    UNSURPPORTEDCHANNEL("05");

    private String code;//GF200301000116

    public static ProcessSt findByCode(String code){
        if(code == null) return null;

        for(ProcessSt processSt : values()){
            if(code.equals(processSt.getCode())){
                return processSt;
            }
        }
        return null;
    }
}
