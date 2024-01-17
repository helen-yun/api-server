package com.platfos.pongift.data.ssm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 발송 상품 관리/정보
 * </p>
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public class SSMGoodsInfo {
    private String goodsId;
    private String serviceId;
    private String goodsNm;
    private String giftcardTp;
    private BigDecimal salePrice;
    private BigDecimal retailPrice;
    private String goodsInfo;
    private String exchangePlace;
    private String approvalSt;
    private String approvalDt;
    private String expiryGb;
    private LocalDateTime regDate;
    private String regId;
    private LocalDateTime modDate;
    private String modId;
    
    private String goodsImage;
    private String goodsDetailImage;
    private String serviceNm;
    private String centerTel;
}
