package com.platfos.pongift.data.sim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMPolicyInfo {
    private String policyId;
    private String nextExpiry;
    private String applyExpiry;
    private String expiryPeriod;
    private String nextRefund;
    private String applyRefund;
    private String refundRate;
    private String expireDt;
    private String nextWithdraw;
    private String applyWithdraw;
    private String withdrawPeriod;
    private String nextRetract;
    private String applyRetract;
    private String retractPeriod;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;

    /**
     * 상품권 정책 관리 조회 결과
     */
    private int result;
    /**
     * 상품권 정책 관리 조회값
     */
    private int value;
}
