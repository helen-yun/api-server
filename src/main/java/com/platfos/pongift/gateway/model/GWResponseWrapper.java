package com.platfos.pongift.gateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.platfos.pongift.gateway.response.GWResponse;
import com.platfos.pongift.goods.model.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GWResponseWrapper<T> {
    private GWResponse response;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Page page;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> list;

    public GWResponseWrapper(){}
    public GWResponseWrapper(boolean success, String message){
        response = GWResponse.builder().success(success).message(message).build();
    }

    public static GWResponseWrapper build(boolean success, String message){
        return new GWResponseWrapper(success, message);
    }
}
