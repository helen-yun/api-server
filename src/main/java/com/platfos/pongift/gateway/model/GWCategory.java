package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWCategory {
    @Description("상품 카테고리 코드")
    private String type;
    @Description("상품 카테고리 이름(full path)")
    private String name;
}
