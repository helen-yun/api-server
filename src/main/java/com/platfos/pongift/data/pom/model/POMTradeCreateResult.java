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
public class POMTradeCreateResult {
    private String resultCode;
    private String resultMessage;
    private POMTrade pomTrade;
}
