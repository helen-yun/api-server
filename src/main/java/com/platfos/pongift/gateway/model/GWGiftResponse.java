package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGiftResponse {
    @Description("바코드번호")
    private String barcodeNo;

    @Description("상품 거래 ID")
    private String productOrderId;

    @DateTime(format = "yyyyMMdd")
    @Description("유효기간")
    private String expiryDate;

}
