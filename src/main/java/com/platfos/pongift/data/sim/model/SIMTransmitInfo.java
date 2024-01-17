package com.platfos.pongift.data.sim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.definition.SIMTransmitInfoSendSt;
import com.platfos.pongift.wideshot.definition.WideShot;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMTransmitInfo {
    private String transmitId;
    private String dispatchId;
    private String templateId;
    private String requestCd;
    private String resultCd;
    private String sendTp;
    private String sendSt;
    private String sendDt;
    private int inquiryCnt;
    private boolean timeOver;
    private Date regDate;

    public String toString(String requestMessage, String resultMessage) {
        return "문자전송ID : " + transmitId + System.lineSeparator() +
                "발송ID : " + dispatchId + System.lineSeparator() +
                "템플릿ID : " + templateId + System.lineSeparator() +
                "요청 응답 : " + requestMessage + System.lineSeparator() +
                "결과 응답 : " + resultMessage + System.lineSeparator() +
                "발송 유형 : " + WideShot.findBySendTp(sendTp) + System.lineSeparator() +
                "발송 상태 : " + SIMTransmitInfoSendSt.findByCode(sendSt).name() + System.lineSeparator() +
                "결과 조회 시도 수 : " + inquiryCnt +"회" + System.lineSeparator() +
                "결과 시간 만료 상태 : " + (timeOver?"만료":"-") ;
    }
}
