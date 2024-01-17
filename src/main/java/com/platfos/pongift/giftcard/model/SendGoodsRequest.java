package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendGoodsRequest {
    @Require
    @Description("상품ID")
    private String goodsId;

    @Require
    @Description("매장ID")
    private String storeId;

    @Require
    @Description("수신자 번호")
    private String receiverPhone;

    @Require
    @Description("수신자 명")
    private String receiverNm;
}
