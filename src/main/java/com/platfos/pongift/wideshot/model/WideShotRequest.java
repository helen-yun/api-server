package com.platfos.pongift.wideshot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface WideShotRequest {
    @JsonIgnore
    public MultiValueMap<String, Object> getParams(Map<String, Object> info);
}
