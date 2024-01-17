package com.platfos.pongift.data.pom.model;

import com.platfos.pongift.data.gim.model.GIMGoods;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POMTrade {
    private String tradeId;
    private String merchantId;
    private String serviceId;
    private String storeId;
    private String goodsId;
    private String ledgerId;
    private String tradeTp;
    private String businessTp;
    private String serviceTp;
    private String orderNo;
    private String detailNo;
    private String channelNm;
    private String supplyNm;
    private int orderAmt;
    private String orderDt;
    private String orderTm;
    private String approvalNo;
    private int orderCnt;
    private int discountAmt;
    private String approvalDt;
    private String approvalTm;
    private String paymentGb;
    private String jubileeTp;
    private String messageInfo;
    private double salesRate;
    private double channelRate;
    private double paymentRate;
    private double useRate;
    private double agencyRate;
    private int salesWon;
    private int channelWon;
    private int paymentWon;
    private int useWon;
    private int agencyWon;
    private double rateAmt;
    private double agencyAmt;
    private String vatFl;
    private String agencyVat;
    private String agencyId;
    private String remitBasic;
    private String adjustCycle;
    private String addfareId;
    private Date regDate;

    private String[] tradeTps;
}
