package com.platfos.pongift.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** PK 생성 프로시저 오류 **/
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CreateInexException extends Exception {
    public CreateInexException(String message){
        super(message);
    }
}
