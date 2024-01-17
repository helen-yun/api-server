package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 응답코드 연동 방식
 * (01:pongiftAPI, 02:제휴사API, 03:유통채널사API, 04:SDK연동, 05:SI)
 */
@Getter
@AllArgsConstructor
public enum SIMReplyCodeAPIGB {
    API("01"),
    DISTRIBUTORCHANNEL("02"),
    SDK("03"),
    SI("04"),
    ETC("99");

    private String code;
}
