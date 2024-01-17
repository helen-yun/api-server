package com.platfos.pongift.store.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Conditional;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Conditional(selected = "timeTp", value = "01", targets = { "openingTm", "endingTm"})
public class StoreOperate {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = { GroupD.class })
    @SimIndex(simIndex = SIMIndex.MSM_OPERATE_INFO)
    @Description("매장운영정보ID")
    private String operateId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = { GroupD.class })
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.dayGb)
    @Description("매장 운영시간 요일 구분")
    private String dayGb;

    //@ParamRequireDependence(field = "openingTm", value = "01"), @ParamRequireDependence(field = "endingTm", value = "01")
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.timeTp)
    @Description("매장 운영시간 시간 유형")
    private String timeTp;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.openingTm)
    @Description("매장 운영시간 시작 시간")
    private String openingTm;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.endingTm)
    @Description("매장 운영시간 종료 시간")
    private String endingTm;

    @ParamGroup(groups = {GroupD.class})
    @Description("등록 일시")
    private Date regDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("등록자")
    private String regId;

    @ParamGroup(groups = {GroupD.class})
    @Description("변경 일시")
    private Date modDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("변경자")
    private String modId;
}