package com.platfos.pongift.wideshot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.platfos.pongift.validate.annotation.Description;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WideShotResponse {
    @Description("결과 코드")
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Description("결과 메시지")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Description("결과 데이터")
    private Object data;

    @JsonIgnore
    public boolean isSuccess(){
        return (!StringUtils.isEmpty(code) && code.equals("200"));
    }
}
