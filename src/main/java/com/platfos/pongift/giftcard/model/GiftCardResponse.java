package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.constraints.SimIndex;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCardResponse {
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @SimIndex(simIndex = {SIMIndex.GIM_GOODS, SIMIndex.SSM_GOODS_INFO})
    @Description("상품ID")
    private String goodsId;

    @SimCode(groupCode = SIMCodeGroup.giftSt)
    @Description("상품권 상태")
    private String giftSt;

    @Description("거래금액(상품권 금액)")
    private int tradePrice;
}
