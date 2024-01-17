package com.platfos.pongift.data.ssm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * <p>
 * 발송 내역 정보
 * </p>
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public class SSMSendInfo extends SSMGoodsInfo {
    private String sendId;
    private int groupNo;
    private String giftcardNo;
    private String receiverNm;
    private String receiverPhone;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String orderNo;
    private String resendCnt;
    private String accessFl;
    private String centerTel;
    private boolean sendTalkSt;
}
