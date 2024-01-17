package com.platfos.pongift.gateway.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GWResponseData<T> extends GWResponseBase {
    private T data;
}
