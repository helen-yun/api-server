package com.platfos.pongift.response.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse<T> extends BaseResponse {
    private T data;
}
