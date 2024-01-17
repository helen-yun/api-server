package com.platfos.pongift.goods.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 상품 전시 상태(대기)
 */
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GoodsExhibitStWaittingException extends Exception {
    private String goodsName;

    public GoodsExhibitStWaittingException(String goodsName){
        super(!StringUtils.isEmpty(goodsName)?goodsName:"");
        this.goodsName = goodsName;
    }
}
