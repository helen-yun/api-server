package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWTradeUseRequest {
    @Require
    @Description("거래ID")
    private String tradeId;

    @Require
    @Description("상품 거래 ID")
    private String productOrderId;

    @Require
    @Description("상품권 PK")
    private String ledgerId;

    @Require
    @Description("상품권 번호")
    private String giftNo;
}
