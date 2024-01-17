package com.platfos.pongift.data.cim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CIMLedger {
    private String ledgerId;
    private String serviceId;
    private String giftNo;
    private String giftNoAes256;
    private int giftAmt;
    private String bareNo;
    private int expiryDay; // 2022-04-25 유효기간 일 단위로 변경 요청
    private String expiryDt;
    private String giftSt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int sendCnt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int remainDay;
    private Date regDate;
    private Date modDate;
}
