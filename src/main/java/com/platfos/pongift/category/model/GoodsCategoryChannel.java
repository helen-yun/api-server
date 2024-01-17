package com.platfos.pongift.category.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.definition.DataDefinition;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * 상품(전시) 카테고리의 유통채널 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCategoryChannel {
    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("전시카테고리ID")
    private String exhibitId;

    @Require
    @Description("유통 채널 구분")
    private String channelGb;

    @ParamGroup(groups = GroupD.class)
    @Description("유통채널명")
    private String channelNm;

    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("유통채널 상품 카테고리 코드")
    private String channelCategory;

    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("유통채널 상품 카테고리명")
    private String channelCategoryNm;

    @Require
    @Min(0)
    @Max(DataDefinition.MAX_SMALLINT)
    @Description("유통채널 상품 재고 수량")
    private int stockCnt;

    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("등록 일시")
    private Date regDate;

    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("변경 일시")
    private Date modDate;
}
