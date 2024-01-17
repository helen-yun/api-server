package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWTrade {
    @Require
    @Description("거래ID")
    private String tradeId;

    @SimCode(groupCode = { SIMCodeGroup.tradeTp })
    @Description("거래유형")
    private String tradeTp;

    @DateTime(format = "yyyyMMddHHmmss")
    @Description("거래 일시")
    private String tradeDate;

    private List<GWTradeListGoods> goodsList;
}
