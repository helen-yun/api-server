package com.platfos.pongift.goods.exception;


import com.platfos.pongift.definition.ResponseCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 유통채널 상품 처리 상태 오류
 */
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GoodsControlException extends Exception {
    private ResponseCode responseCode;
    private String channelName;
    private String goodsName;

    public GoodsControlException(ResponseCode responseCode, String channelName, String goodsName){
        super("{" +
                "responseCode='" + responseCode.name() + '\'' +
                ", channelName='" + (!StringUtils.isEmpty(channelName)?channelName:"") + '\'' +
                ", goodsName='" + (!StringUtils.isEmpty(goodsName)?goodsName:"") + '\'' +
                '}');
        this.responseCode = responseCode;
        this.channelName = channelName;
        this.goodsName = goodsName;
    }
}
