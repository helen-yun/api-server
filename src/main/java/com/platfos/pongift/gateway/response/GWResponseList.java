package com.platfos.pongift.gateway.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GWResponseList<T> extends GWResponseBase {
    private List<T> list;
}
