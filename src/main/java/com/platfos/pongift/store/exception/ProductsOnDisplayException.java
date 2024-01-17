package com.platfos.pongift.store.exception;


import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProductsOnDisplayException extends Exception {
    public ProductsOnDisplayException(){
        super("");
    }
}
