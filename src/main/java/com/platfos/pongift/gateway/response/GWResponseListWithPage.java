package com.platfos.pongift.gateway.response;

import com.platfos.pongift.goods.model.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GWResponseListWithPage<T> extends GWResponseBase {
    private Page page;
    private List<T> list;
}
