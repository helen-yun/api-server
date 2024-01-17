package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.group.GroupA;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGoodsExhibitSt {
    @ParamGroup(groups = GroupA.class)
    @Require(groups = GroupA.class)
    @SimCode(groupCode = SIMCodeGroup.channelGb)
    @Description("유통 채널 구분")
    private String channelGb;

    @Require
    @Description("상품ID (유통채널)")
    private String goodsId; // TODO: 향후 삭제 - 폰기프트 상품 ID와 이름 혼동 우려가 매우 큼

    @Require
    @SimCode(groupCode = SIMCodeGroup.exhibitSt)
    @Description("상품 전시 상태")
    private String exhibitSt;
    
    @Require
    @Description("채널 상품 ID")
    private String channelId;
}
