package com.platfos.pongift.category.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCategory {
    @ParamGroup(groups = GroupD.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String goodsCategory;

    @Require
    @Description("상품 카테고리 코드")
    private String goodsCategoryCd;

    @ParamGroup(groups = GroupD.class)
    @Description("상품 카테고리 레벨")
    private int level;

    @ParamGroup(groups = GroupD.class)
    @Description("상품 카테고리명")
    private String goodsCategoryNm;

    @Description("지원 유통채널 리스트")
    private List<GoodsCategoryChannel> channels;

    @Override
    public String toString() {
        return "GoodsCategory{" +
                "goodsCategory='" + goodsCategory + '\'' +
                ", goodsCategoryCd='" + goodsCategoryCd + '\'' +
                ", level=" + level +
                ", goodsCategoryNm='" + goodsCategoryNm + '\'' +
                '}';
    }
}
