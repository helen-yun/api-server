package com.platfos.pongift.goods.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.category.model.GoodsCategory;
import com.platfos.pongift.category.model.GoodsCategoryChannel;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.gim.model.GIMGoods;
import com.platfos.pongift.data.gim.model.GIMGoodsControl;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.*;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupC;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goods {
    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @Description("[%goodsCategory%]")
    private GoodsCategory category;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = GroupD.class)
    private List<GoodsChannel> channels;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = GroupD.class)
    @SimIndex(simIndex = SIMIndex.MIM_SERVICE)
    @Description("서비스ID")
    private String serviceId;

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @ParamGroup(groups = { GroupB.class, GroupC.class })
    @Require(groups = { GroupB.class, GroupC.class })
    @SimIndex(simIndex = {SIMIndex.GIM_GOODS, SIMIndex.SSM_GOODS_INFO})
    @Description("상품ID")
    private String goodsId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = GroupD.class)
    private String goodsCategory;

    @ParamGroup(groups = GroupA.class)
    @Require(groups = GroupA.class)
    @SimCode(groupCode = SIMCodeGroup.giftcardTp)
    @Description("상품권 유형")
    private String giftcardTp;

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @Length(max = 50)
    @Description("상품명")
    private String goodsNm;

    @Min(0)
    @Max(DataDefinition.MAX_SMALLINT)
    @Description("재고 수량")
    private Integer stockCnt;

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @Min(0)
    @Max(Integer.MAX_VALUE-1)
    @Description("소비자 가격 (정상가)")
    private Integer retailPrice;

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @Min(0)
    @Max(Integer.MAX_VALUE-1)
    @Description("실 판매가")
    private Integer salePrice;

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @Min(0)
    @Max(Integer.MAX_VALUE-1)
    @Description("상품 공급가")
    private Integer supplyPrice;

    /*@ParamFieldValidate(methodRole = { @MethodRole(methodRoleType = MethodRoleType.INSERT), @MethodRole(methodRoleType = MethodRoleType.UPDATE) },
            require = true, min = 0, maxD = Double.MAX_VALUE-0.001d)
    private Double chargeRate;*/

    @ParamGroup(groups = { GroupA.class, GroupB.class })
    @Require(groups = { GroupA.class, GroupB.class })
    @SimCode(groupCode = SIMCodeGroup.taxFl)
    @Description("과세 여부")
    private String taxFl;

    @ParamGroup(groups = GroupD.class)
    @SimCode(groupCode = SIMCodeGroup.goodsApprovalSt)
    @Description("승인 상태")
    private String approvalSt;

    @ParamGroup(groups = GroupD.class)
    @DateTime(format = "yyyyMMdd")
    @Description("승인 상태 변경 일자")
    private String approvalDt;

    @ParamGroup(groups = GroupD.class)
    @SimCode(groups = GroupD.class, groupCode = SIMCodeGroup.exhibitSt)
    @Description("상품 전시 상태")
    private String exhibitSt;

    @Require
    @Length(max = 100)
    @Description("교환처")
    private String exchangePlace;

    @Require
    @Length(max = 500)
    @Description("상품 정보")
    private String goodsInfo;

    @Length(max = 500)
    @Description("주의 사항")
    private String cautionPoint;

    @Length(max = 500)
    @Description("제한 사항")
    private String limitPoint;

    @ParamGroup(groups = GroupD.class)
    @Require(groups = GroupD.class)
    @SimCode(groupCode = SIMCodeGroup.expiryGb)
    @Description("유효 기간 구분(단위 : 년)")
    private String expiryGb;

    @ParamGroup(groups = GroupD.class)
    @SimCode(groupCode = SIMCodeGroup.goodsProcessSt)
    @Description("처리 상태")
    private String processSt;

    @Require
    @File(supportAttachType = { AttachDataType.URL, AttachDataType.BASE64 }, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"}, minImageWidth = 600, minImageHeight = 600, maxImageWidth = 1000, maxImageHeight = 1000)
    @Description("대표이미지")
    private String goodsImage;

    @File(supportAttachType = { AttachDataType.URL, AttachDataType.BASE64 }, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("상세이미지")
    private String goodsDetailImage;

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

    @JsonIgnore
    public GIMGoods gimGoods(){
        GIMGoods gimGoods = GIMGoods.builder()
                .goodsId(getGoodsId())
                .serviceId(getServiceId())
                .storeId(getStoreId())
                .goodsCategory(getGoodsCategory())
                .giftcardTp(getGiftcardTp())
                .goodsNm(getGoodsNm())
//                .stockCnt(getStockCnt())
                .goodsOption("N")
                .optionCnt(null)
                .sortOrder(null)
                .retailPrice(getRetailPrice())
                .salePrice(getSalePrice())
                .supplyPrice(getSupplyPrice())
                //.chargeRate(getChargeRate())
                .taxFl(getTaxFl())
                .exhibitSt(getExhibitSt())
                .exchangePlace(getExchangePlace())
                .goodsInfo("")
                .cautionPoint("")
                .limitPoint("")
                .expiryGb(getExpiryGb())
                .goodsImage(getGoodsImage())
                .goodsDetailImage(getGoodsDetailImage())
                .build();
        return gimGoods;
    }

    @JsonIgnore
    public List<GIMChannelInfo> gimChannelInfos(){
        List<GIMChannelInfo> gimChannelInfos = new ArrayList<>();
        if(!ObjectUtils.isEmpty(getCategory().getChannels())){
            for(GoodsCategoryChannel channel : getCategory().getChannels()){
                gimChannelInfos.add(GIMChannelInfo.builder()
                        .goodsId(getGoodsId())
                        .channelGb(channel.getChannelGb())
                        .stockCnt(channel.getStockCnt()) /* TODO: 임시 처리 (채널 별 재고 설정 값이 들어가야 함) */
                        .build());
            }
        }
        return gimChannelInfos;
    }

    @JsonIgnore
    public List<GIMGoodsControl> gimGoodsControls(){
        List<GIMGoodsControl> gimGoodsControls = new ArrayList<>();
        if(!ObjectUtils.isEmpty(getCategory().getChannels())){
            for(GoodsCategoryChannel channel : getCategory().getChannels()){
                gimGoodsControls.add(GIMGoodsControl.builder()
                        .goodsId(getGoodsId())
                        .channelGb(channel.getChannelGb())
                        .build());
            }
        }
        return gimGoodsControls;
    }
}
