package com.platfos.pongift.trade.exception;


import com.platfos.pongift.definition.ResponseCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TradeException extends Exception {
    private ResponseCode responseCode;
    private String addMessage;

    public TradeException(ResponseCode responseCode){
        super(responseCode.name());
        this.responseCode = responseCode;
    }
    public TradeException(ResponseCode responseCode, String addMessage){
        super(responseCode.name()+(!StringUtils.isEmpty(addMessage)?" : "+addMessage:""));
        this.responseCode = responseCode;
        this.addMessage = addMessage;
    }
}
