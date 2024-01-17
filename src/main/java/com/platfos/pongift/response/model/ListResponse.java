package com.platfos.pongift.response.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResponse<T> extends BaseResponse {
    private List<T> list;
}
