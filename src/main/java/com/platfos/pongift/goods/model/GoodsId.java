package com.platfos.pongift.goods.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsId {
    @Require
    @SimIndex(simIndex = {SIMIndex.GIM_GOODS, SIMIndex.SSM_GOODS_INFO})
    @Description("상품ID")
    private String goodsId;
}
