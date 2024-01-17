package com.platfos.pongift.authorization.exception;

/**
 * Exception : API 권한 없음 (해당 로그 생성 안함)
 */
public class APIPermissionAuthExceptionForNoLogger extends IllegalArgumentException {
    public APIPermissionAuthExceptionForNoLogger(){
        super();
    }
    public APIPermissionAuthExceptionForNoLogger(String message){
        super(message);
    }
}
