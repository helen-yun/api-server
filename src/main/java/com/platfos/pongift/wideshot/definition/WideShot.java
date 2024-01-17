package com.platfos.pongift.wideshot.definition;

import com.platfos.pongift.definition.SIMTransmitInfoSendSt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
@AllArgsConstructor
public enum WideShot {
    SMS("/api/v1/message/sms", "POST", null, "01"),
    LMS("/api/v1/message/lms", "POST", null, "02"),
    MMS("/api/v1/message/mms", "POST", MediaType.MULTIPART_FORM_DATA, "03"),
    ALIMTALK("/api/v1/message/alimtalk", "POST", null, "04"),
    RESULT("/api/v1/message/result", "GET", null, null),
    RESULTS("/api/v1/message/results", "GET", null, null);

    private String url;
    private String method;
    private MediaType contentType;
    private String sendTp;

    public static WideShot findBySendTp(String sendTp){
        if(sendTp == null) return null;

        for(WideShot wideShot : values()){
            if(sendTp.equals(wideShot.getSendTp())){
                return wideShot;
            }
        }
        return null;
    }
}
