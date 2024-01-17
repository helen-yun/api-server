package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.DataDefinition;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWTradeListGoods {
    @Description("상품 거래 ID")
    private String productOrderId;

    @Require
    @Description("상품ID")
    private String goodsId;

    @Require
    @Length(max = 50)
    @Description("상품명")
    private String goodsNm;

    @Require
    @Min(0)
    @Max(DataDefinition.MAX_SMALLINT)
    @Description("거래수량")
    private int saleCnt;

    @Require
    @Min(0)
    @Max(Integer.MAX_VALUE-1)
    @Description("실판매가")
    private int salePrice;
}
