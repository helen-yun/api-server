package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.*;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.File;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGoodsRequest {
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.channelGb)
    @Description("유통 채널 구분")
    private String channelGb;

    @ParamGroup(groups = {GroupB.class})
    @Require(groups = {GroupB.class})
    @Description("상품ID (유통채널)")
    private String goodsId;

    @Require
    @Size(min = 1, max = 10)
    @Description("상품 카테고리 코드 리스트")
    private List<String> categories;

    @Require
    @Length(max = 50)
    @Description("상품명")
    private String goodsNm;

    @Require
    @Min(0)
    @Max(DataDefinition.MAX_SMALLINT)
    @Description("재고 수량")
    private Integer stockCnt;

    @Require
    @Min(0)
    @Max(Integer.MAX_VALUE - 1)
    @Description("실 판매가")
    private Integer salePrice;

    @Require
    @Min(0)
    @Max(Integer.MAX_VALUE - 1)
    @Description("소비자 가격 (정상가)")
    private Integer retailPrice;

    @Require
    @Min(0)
    @Max(Integer.MAX_VALUE - 1)
    @Description("할인가")
    private Integer discountPrice;

    @Require
    @SimCode(groupCode = SIMCodeGroup.exhibitSt)
    @Description("상품 전시 상태")
    private String exhibitSt;    
    
    @Require
    @SimCode(groupCode = SIMCodeGroup.expiryGb)
    @Description("유효기간")
    private String expiryGb;

    @File(supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"}, supportAttachType = AttachDataType.URL)
    @Description("대표이미지")
    private String goodsImage;

    @Description("상품 상세 이미지")
    private String infoUrl;

    @Description("매장 상세 이미지")
    private String storeUrl;

    @Require
    @Description("상품 상세 내용(html)")
    private String html;

    @Description("매장 이름")
    private String storeName;

    @Description("매장 ID")
    private String storeId;

    @Description("제휴몰 매장 PK")
    private long storeSeq;
}
