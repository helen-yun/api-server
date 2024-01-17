package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.CustomCode;
import com.platfos.pongift.validate.constraints.PhoneNumber;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupC;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResendGiftCard {
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupB.class})
    @SimIndex(simIndex = SIMIndex.CIM_LEDGER)
    @Description("상품권원장ID")
    private String ledgerId;

    @ParamGroup(groups = {GroupB.class})
    @Require(groups = {GroupB.class})
    @PhoneNumber
    @Description("휴대폰번호")
    private String phoneNo;

    @ParamGroup(groups = {GroupC.class})
    @Require(groups = {GroupC.class})
    @Description("상품권 거래 유형")
    private String tradeTp;

    @ParamGroup(groups = {GroupC.class})
    @Require(groups = {GroupC.class})
    @Description("거래주문번호")
    private String orderNo;

    @ParamGroup(groups = {GroupC.class})
    @Require(groups = {GroupC.class})
    @Description("채널구분")
    private String channelGb;
}
