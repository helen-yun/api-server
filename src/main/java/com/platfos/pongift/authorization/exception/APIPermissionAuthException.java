package com.platfos.pongift.authorization.exception;

/**
 * Exception : API 권한 없음
 */
public class APIPermissionAuthException extends IllegalArgumentException {
    public APIPermissionAuthException(){
        super();
    }
    public APIPermissionAuthException(String message){
        super(message);
    }
}
