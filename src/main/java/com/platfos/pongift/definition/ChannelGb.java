package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelGb {
    /**
     * 네이버
     */
    NAVER("01"),
    /**
     * 옥션
     */
    AUCTION("02"),
    /**
     * 이베이
     */
    EBAY("03"),
    /**
     * 위메프
     */
    WEMAKEPRICE("04"),
    /**
     * SSG
     */
    SSG("05"),
    /**
     * 다파라 ( 테스트용 유통채널 )
     */
    DAPARA("06"),
    /**
     * 플랫포스 (직발송 유통채널 )
     */
    PLATFOS("07");
    
    private String code;

    public static ChannelGb findByCode(String code) {
        if (code == null) return null;

        for (ChannelGb channelGb : values()) {
            if (code.equals(channelGb.getCode())) {
                return channelGb;
            }
        }
        return null;
    }
}
