package com.platfos.pongift.data.pom.model;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("POMTradeSeqNo")
public class POMTradeSeqNo {
    private String inputCd;
    private String tradeSeqNo;
    private String resultCode;
    private String resultMsg;
}
