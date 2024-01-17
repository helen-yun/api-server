package com.platfos.pongift.wideshot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.wideshot.definition.WideShot;
import lombok.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WideShotResultRequest implements WideShotRequest {
    @Require
    @Description("발송코드 (문자 고유 식별자)")
    private String sendCode;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private WideShot wideShotType;

    @Override
    public MultiValueMap<String, Object> getParams(Map<String, Object> info) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("sendCode", sendCode);

        return map;
    }
}
