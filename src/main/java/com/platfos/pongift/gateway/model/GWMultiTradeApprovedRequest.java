package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWMultiTradeApprovedRequest {
    @Require
    @Description("유통 채널 구분")
    @SimCode(groupCode = SIMCodeGroup.channelGb)
    private String channelGb;

    @Require
    @Description("거래ID")
    private String tradeId;

    @Require
    @DateTime(format = "yyyyMMddHHmmss")
    @Description("거래(구매/구매취소/선물하기) 일시")
    private String tradeDate;

    @Require
    @Valid
    private GWBuyer buyer;

    @Require
    @Valid
    private GWTradeGoods goods;

    @Require
    @Description("다건 발송 수신자 리스트")
    private List<GWMultiTrade> multiTradeList;
}
