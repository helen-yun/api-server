package com.platfos.pongift.goods.model;

import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {
    @Description("결과 총 페이지 수")
    private int totalPage;
    @Description("조회 페이지")
    private int page;
}
