package com.platfos.pongift.gateway.model;

import com.platfos.pongift.goods.model.Page;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWTradeList {
    private Page page;
    private List<GWTrade> trades;
}
