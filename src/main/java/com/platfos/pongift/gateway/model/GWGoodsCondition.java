package com.platfos.pongift.gateway.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWGoodsCondition {
    private String categoryTp;
    private String approvalSt;
    private String keywordGoodsNm;
    private String page;
    private String pageSize;
    private String orderCd;
}
