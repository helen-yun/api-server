package com.platfos.pongift.trade.model;

import com.platfos.pongift.definition.GiftCardTp;
import com.platfos.pongift.definition.TradeTp;
import com.platfos.pongift.gateway.model.GWBuyer;
import com.platfos.pongift.gateway.model.GWReceiver;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {
    private String tradeId;
    private String serviceId;
    private String storeId;
    private String supplyServiceId;
    private String serviceNm;
    private String centerTel;
    private String goodsId;
    private String goodsNm;
    private String goodsImage;
    private String exchangePlace;
    private String goodsInfo;
    private String orderNo;
    private String detailNo;
    private int orderPrice;
    private String orderDate;
    private int orderCnt;
    private int retailPrice;
    private GWBuyer buyer;
    private GWReceiver receiver;
    private GiftCardTp giftCardTp;
    private TradeTp tradeTp;
    private String expiryGb;
    private String channelId;
}
