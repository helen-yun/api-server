package com.platfos.pongift.goods.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsList {
    private Page page;
    private List<Goods> goods;
}
