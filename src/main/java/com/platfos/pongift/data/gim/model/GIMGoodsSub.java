package com.platfos.pongift.data.gim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GIMGoodsSub {
    private String optionId;
    private String goodsId;
    private String optionNm;
    private String optionDesc;
    private String priceTp;
    private int optionPrice;
    private int stockCnt;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;
}
