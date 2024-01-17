package com.platfos.pongift.data.ssm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SSMSmsTemplateValues {
    private String title;
    private String giftResendCnt;
    private String giftMaxResendCnt;
    private String serviceNm;
    private String centerTel;
    private String contents;
    private String goodsImage;
    private String goodsNm;
    private String giftExpiryDt;
    private String guide;
    private String giftBarcode;
    private String buyerPhoneNo;
    private String receiverPhoneNo;
    private String publicLegerId;
    private String goodsInfo;
    private String exchangePlace;
    private String expireDt;
    private String customerCenterTel;
    private String giftStampDutyInfo;
    private String publicCode;
    private String purchaseDt;
    private String purchaseCnt;
    private String purchaseAmount;
    private String giftAmtText;
    private String giftAmt;
    private String sendTalkSt;
    private String expiryDtY4MMDD;
    private String pastId;
}
