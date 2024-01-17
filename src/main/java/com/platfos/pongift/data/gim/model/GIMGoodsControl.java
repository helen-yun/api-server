package com.platfos.pongift.data.gim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GIMGoodsControl {
    private String controlId;
    private String goodsId;
    private String channelGb;
    private String processSt;
    private int executeCnt;
    private Date regDate;
    private Date modDate;

    @Override
    public String toString() {
        return "GIMGoodsControl{" +
                "controlId='" + controlId + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", channelGb='" + channelGb + '\'' +
                ", processSt='" + processSt + '\'' +
                ", executeCnt=" + executeCnt +
                ", regDate=" + regDate +
                ", modDate=" + modDate +
                '}';
    }
}
