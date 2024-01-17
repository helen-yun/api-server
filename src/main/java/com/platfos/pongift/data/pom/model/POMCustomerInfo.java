package com.platfos.pongift.data.pom.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POMCustomerInfo {
    private String customerId;
    private String productId;
    private String userId;
    private String buyerNm;
    private String buyerPhone;
    private String buyerId;
    private String receiverNm;
    private String receiverPhone;
    private String receiverId;
    private String certifyNo;
    private Date regDate;
}
