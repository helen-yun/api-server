package com.platfos.pongift.data.gim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GIMChannelInfo {
    private String routeId;
    private String goodsId;
    private String channelGb;
    private String channelNm;
    private String channelId;
    private int stockCnt;
    private String useFl;
    private String exhibitSt;
    private String exhibitEnd;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;

}
