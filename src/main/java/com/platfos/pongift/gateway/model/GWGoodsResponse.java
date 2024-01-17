package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.DateTime;
import com.platfos.pongift.validate.constraints.SimCode;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGoodsResponse {
    @Description("상품ID")
    private String goodsId;

    @Description("상품 카테고리")
    private List<GWCategory> categories;

    @Description("상품명")
    private String goodsNm;

    @Description("재고 수량")
    private int stockCnt;

    @Description("실 판매가")
    private int salePrice;
    @Description("소비자 가격 (정상가)")
    private int retailPrice;

    @Description("상품 전시 상태")
    @SimCode(groupCode = SIMCodeGroup.exhibitSt)
    private String exhibitSt;

    @Description("전시판매시작일")
    @DateTime(format = "yyyyMMdd")
    private String saleStartDate;

    @Description("전시판매종료일")
    @DateTime(format = "yyyyMMdd")
    private String saleEndDate;

    @Description("대표 이미지")
    private String goodsImage;
    @Description("상품 상세 내용(html)")
    private String html;

    @Description("등록 일시")
    private Date regDate;
    @Description("변경 일시")
    private Date modDate;
}
