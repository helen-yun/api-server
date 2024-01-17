package com.platfos.pongift.gateway.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWTradeDetailRequest {
    private String tradeId;
    private String productOrderId;
    // 반품 거부 사유
    private String rejectReturnReason;
}
