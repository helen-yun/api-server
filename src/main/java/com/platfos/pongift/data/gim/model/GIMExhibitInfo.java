package com.platfos.pongift.data.gim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GIMExhibitInfo {
    private String exhibitId;
    private String goodsCategory;
    private String channelGb;
    private String channelCategory;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;
}
