package com.platfos.pongift.gateway.model;


import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGiftExtensionRequest {
    @Require
    @Description("거래ID")
    private String tradeId;

    @Require
    @Description("상품 거래 ID")
    private String productOrderId;

    @Require
    @Description("상품권원장ID")
    private String ledgerId;

    @Require
    @DateTime(format = "yyyyMMdd")
    @Description("유효기간")
    private String expiryDate;

    @Require
    @Description("상품권 번호")
    private String giftNo;
}
