package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCardTrade {
    @Require
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @Require
    @Description("모바일상품권(바코드)번호")
    private String barcode;

    @Require
    @Description("거래주문번호(API 사용자가 생성한 주문 고유 식별자)")
    private String orderNo;

    @Require
    @DateTime(format = "yyyyMMdd")
    @Description("거래일자")
    private String orderDt;

    @Require
    @DateTime(format = "HHmmss")
    @Description("거래시간")
    private String orderTm;
}
