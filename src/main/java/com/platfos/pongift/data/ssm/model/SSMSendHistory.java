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
public class SSMSendHistory extends SSMSendInfo {
    private String pastId;
    private String sendGb;
    private String requestCd;
    private String resultCd;
    private LocalDateTime regDate;
}
