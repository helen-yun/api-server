package com.platfos.pongift.giftcard.exeption;


import com.platfos.pongift.definition.ResponseCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 상품권 사용/사용취소/발송/재발송 작업 중 유효하지 않는 경우 및 오류 **/
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GiftException extends Exception {
    /** 응답 코드 **/
    private ResponseCode responseCode;
    /** 추가 옵션(메시지) **/
    private String option;

    public GiftException(ResponseCode responseCode){
        super(responseCode.name());
        this.responseCode = responseCode;
    }
    public GiftException(ResponseCode responseCode, String option){
        super(responseCode.name()+(!StringUtils.isEmpty(option)?" : "+option:""));
        this.responseCode = responseCode;
        this.option = option;
    }
}
