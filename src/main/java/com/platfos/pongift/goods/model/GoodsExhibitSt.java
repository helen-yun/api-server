package com.platfos.pongift.goods.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.CustomCode;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsExhibitSt {
    @Require
    @SimIndex(simIndex = SIMIndex.GIM_GOODS)
    @Description("상품ID")
    private String goodsId;

    @Require
    @CustomCode(codes = {"Y", "N"})
    @Description("상품 전시 유무")
    private String exhibitSt;
}
