package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.constraints.SimIndex;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGiftExtension {
    @SimCode(groupCode = SIMCodeGroup.channelGb)
    @Description("유통 채널 구분")
    private String channelGb;

    @Require
    @SimIndex(simIndex = SIMIndex.CIM_LEDGER)
    @Description("상품권원장ID")
    private String ledgerId;

    @Require
    @DateTime(format = "yyyyMMdd")
    @Description("유효기간")
    private String expiryDate;
}
