package com.platfos.pongift.goods.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsChannel {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String routeId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String goodsId;
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.channelGb)
    @Description("유통 채널 구분")
    private String channelGb;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String channelId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int stockCnt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String exhibit_end;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String exhibit_st;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date regDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String regId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date modDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String modId;

    public GoodsChannel(GIMChannelInfo gimChannelInfo){
        setRouteId(gimChannelInfo.getRouteId());
        setGoodsId(gimChannelInfo.getGoodsId());
        setChannelGb(gimChannelInfo.getChannelGb());
        setRegDate(gimChannelInfo.getRegDate());
        setRegId(gimChannelInfo.getRegId());
        setModDate(gimChannelInfo.getModDate());
        setModId(gimChannelInfo.getModId());
    }
}
