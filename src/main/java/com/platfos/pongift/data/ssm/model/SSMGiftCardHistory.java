package com.platfos.pongift.data.ssm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SSMGiftCardHistory extends SSMSendInfo {
    private String changeId;
//    private String sendId;
    private String expiryDt;
    private LocalDateTime regDate;
}
