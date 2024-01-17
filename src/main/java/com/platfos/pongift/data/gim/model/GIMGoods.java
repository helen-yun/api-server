package com.platfos.pongift.data.gim.model;

import com.platfos.pongift.data.sim.model.SIMAttachFile;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GIMGoods {
    private String goodsId;
    private String serviceId;
    private String storeId;
    private String goodsCategory;
    private String giftcardTp;
    private String goodsNm;
    private int stockCnt;
    private String goodsOption;
    private String optionCnt;
    private String sortOrder;
    private int retailPrice;
    private int salePrice;
    private int supplyPrice;
    private double chargeRate;
    private String taxFl;
    private String approvalSt;
    private String approvalDt;
    private String exhibitSt;
    private String exchangePlace;
    private String goodsInfo;
    private String cautionPoint;
    private String limitPoint;
    private String expiryGb;
    private String processSt;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;

    private String goodsImage;
    private String goodsDetailImage;
    private String channelId;
}
