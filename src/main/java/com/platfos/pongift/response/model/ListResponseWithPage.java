package com.platfos.pongift.response.model;

import com.platfos.pongift.goods.model.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResponseWithPage<T> extends BaseResponse {
    private Page page;
    private List<T> list;
}
