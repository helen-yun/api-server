package com.platfos.pongift.data.pom.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POMDispatchInfo {
    private String dispatchId;
    private String productId;
    private String serviceId;
    private String customerId;
    private String receiverPhone;
    private String sendGb;
    private int sendCnt;
    private Date regDate;
}
