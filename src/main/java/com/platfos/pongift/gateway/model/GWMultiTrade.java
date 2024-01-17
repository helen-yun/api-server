package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.constraints.Require;
import lombok.*;

import javax.validation.Valid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 다건 발송 수신자 (detailNo 포함)
 */
public class GWMultiTrade {
    @Require
    @Valid
    private GWReceiver receiver;
    @Require
    @Valid
    private String productOrderId;
}
