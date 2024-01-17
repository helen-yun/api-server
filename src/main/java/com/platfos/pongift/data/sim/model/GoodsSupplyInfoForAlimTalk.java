package com.platfos.pongift.data.sim.model;

import lombok.*;

/**
 * 알림톡 전송 요청 정보 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsSupplyInfoForAlimTalk {
    private String serviceNm;
    private String centerTel;
    private String exchangePlace;
    private String goodsInfo;
}
