package com.platfos.pongift.goods.exception;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 매장 서비스 상태 오류
 */
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class StoreServiceStException extends Exception {
    private String storeName;
    private String storeServiceSt;

    public StoreServiceStException(String storeName, String storeServiceSt){
        super((!StringUtils.isEmpty(storeName)?storeName:"") + " : "+(!StringUtils.isEmpty(storeServiceSt)?storeServiceSt:""));
        this.storeName = storeName;
        this.storeServiceSt = storeServiceSt;
    }
}
