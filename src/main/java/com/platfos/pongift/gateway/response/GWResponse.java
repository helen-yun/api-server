package com.platfos.pongift.gateway.response;

import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWResponse {
    @Description("처리 결과 성공 유무")
    private boolean success;
    @Description("응답 메시지")
    private String message;
}
