package com.platfos.pongift.data.pum.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PUMUser {
    private String userId;
    private String userNm;
    private String gnederGb;
    private String birthDt;
    private String phoneNo;
    private String emailAddress;
    private String deliverInfo;
    private String serviceId;
    private String orderFp;
    private String tradeId;
    private String userSt;
    private String joinDt;
    private Date regDate;
}
