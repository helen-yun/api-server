package com.platfos.pongift.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GWTradeApprovedResponse {
    private String giftNo; // 상품권 번호
    private String detailNo; // 승인번호 (스마트스토어 = productOrderId, 제휴몰 = tradeNo)
    private String expiryDate; // 유효기간 날짜
    private String publicCode; // 상품권 공용 코드
}
