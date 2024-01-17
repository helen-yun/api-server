package com.platfos.pongift.data.pom.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POMTradeGift {
    private String productId;
    private String tradeId;
    private String ledgerId;
    private String goodsTp;
    private String goodsNm;
    private String giftcardNo;
    private int goodsPrice;
    private int discountPrice;
    private String useFl;
    private String giftcardSt;
    private String useDt;
    private String expiryDt;
    private String cancelReason;
    private String askDt;
    private String refundDt;
    private String refundNm;
    private String withdrawAccount;
    private String depositorNm;
    private String bankNm;
    private String customerAccount;
    private Date regDate;
    
    // 반품 거부 사유
    private String processDesc;
}
