package com.platfos.pongift.goods.exception;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 상품 승인 상태
 */
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GoodsExhibitNoApprovalStException extends Exception {
    private String goodsName;

    public GoodsExhibitNoApprovalStException(String goodsName){
        super(!StringUtils.isEmpty(goodsName)?goodsName:"");
        this.goodsName = goodsName;
    }
}
