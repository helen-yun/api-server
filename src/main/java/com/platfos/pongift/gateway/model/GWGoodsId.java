package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGoodsId {
    @Require
    @Description("상품ID")
    private String goodsId;

    @Override
    public String toString() {
        return "GWGoodsId{" +
                "goodsId='" + goodsId + '\'' +
                '}';
    }
}
