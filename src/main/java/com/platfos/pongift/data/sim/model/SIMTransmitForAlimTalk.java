package com.platfos.pongift.data.sim.model;

import lombok.*;

/**
 * 알림톡 전송 요청 정보 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMTransmitForAlimTalk {
    private String ledgerId;
    private String dispatchId;
    private String productId;
    private String sendGb;
    private String tradeId;
    private String goodsNm;
    private String tradeTp;
    private String expiryDt;
    private String purchaseDt;
    private int giftAmt;
    private String goodsInfo;
    private String channelGb;
    private String buyerNm;
    private String buyerPhone;
    private String receiverNm;
    private String receiverPhone;
}
