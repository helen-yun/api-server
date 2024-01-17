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
public class GiftCardTradeResponse {
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @SimIndex(simIndex = {SIMIndex.GIM_GOODS, SIMIndex.SSM_GOODS_INFO})
    @Description("상품ID")
    private String goodsId;

    @Require
    @Description("거래주문번호(API 사용자가 생성한 주문 고유 식별자)")
    private String orderNo;

    @Description("거래금액")
    private int tradePrice;

    @Description("거래승인번호")
    private String approvalNo;

    @DateTime(format = "yyyyMMdd")
    @Description("거래승인일자")
    private String approvalDt;

    @DateTime(format = "HHmmss")
    @Description("거래승인시간")
    private String approvalTm;
}
