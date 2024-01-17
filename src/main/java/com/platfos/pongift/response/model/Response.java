package com.platfos.pongift.response.model;

import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {
    @Description("서비스ID")
    private String serviceId;
    @Description("공통 응답 코드")
    private String code;
    @Description("응답 메시지")
    private String message;
}
