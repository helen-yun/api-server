package com.platfos.pongift.goods.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsSearchCondition {
    private String storeId;
    private String goodsCategory;
    private String goodsCategoryCd;
    private String keywordGoodsNm;
    private String exhibitSt;
    private String approvalSt;
    private String processSt;
    private String accessFl;
    private Integer page;
    private Integer pageSize;
    private String orderCd;
    private String agencyId;
}
