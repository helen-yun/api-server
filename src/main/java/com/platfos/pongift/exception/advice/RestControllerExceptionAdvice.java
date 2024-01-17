package com.platfos.pongift.exception.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.definition.AttributeKey;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.definition.AuthorizationHeaderKey;
import com.platfos.pongift.authorization.exception.APIPermissionAuthExceptionForNoLogger;
import com.platfos.pongift.exception.WebException;
import com.platfos.pongift.definition.ResponseCode;
import com.platfos.pongift.giftcard.exeption.GiftException;
import com.platfos.pongift.goods.exception.GoodsControlException;
import com.platfos.pongift.goods.exception.GoodsExhibitNoApprovalStException;
import com.platfos.pongift.goods.exception.GoodsExhibitStWaittingException;
import com.platfos.pongift.goods.exception.StoreServiceStException;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.store.exception.ProductsOnDisplayException;
import com.platfos.pongift.telegram.service.TelegramService;
import com.platfos.pongift.trade.exception.TradeException;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.validate.exception.ParamValidateException;
import com.platfos.pongift.validate.model.ParamValidate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * RestControllerAdvice
 */
@Order(RestControllerExceptionAdvice.ORDER)
@RestControllerAdvice
public class RestControllerExceptionAdvice {
    public static final String CONSTRAINT_REQUIRE_MESSAGE = "CONSTRAINT_REQUIRE_MESSAGE";
    public static final int ORDER = 0;

    private static final Logger logger = LoggerFactory.getLogger(RestControllerExceptionAdvice.class);

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService service;

    /** 텔레그램 서비스 **/
    @Autowired
    TelegramService telegramService;

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, HttpMediaTypeNotAcceptableException e) {
        return service.response(request, ResponseCode.FAIL);
    }

    /** 지원하지 않는 메소드 타입으로 접근 시 **/
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        //405 - Method Not Allowed, 요청 경로는 있으나 지원하지 않는 Method인 경우 발생
        logger.error(e.getMessage(), e);
        sendNotification(request, e);
        return printResponseLog(request, response, service.response(request, ResponseCode.FAIL_METHOD_NOT_ALLOWED));
    }

    /** 지원하지 않는 미디어 타입으로 접근 시 **/
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, HttpMediaTypeNotSupportedException e) {
        //415 - Unsupported Media Type, 요청의 Content Type을 핸들러가 지원하지 않는 경우 발생
        logger.error(e.getMessage(), e);
        sendNotification(request, e);
        return printResponseLog(request, response, service.response(request, ResponseCode.FAIL_UNSUPPORTED_MEDIA_TYPE));
    }

    /** 핸들러가 기대한 요청 Parameter를 찾지 못한 경우 발생 **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException e) {
        String message = e.getMessage();

        if(message.indexOf(":") > -1){
            String[] arr = message.split(":");
            if(!ObjectUtils.isEmpty(arr)){
                if(arr.length == 2) message = arr[0];
                else if(arr.length > 2) message = arr[0]+":"+arr[1];
            }
        }

        return service.response(request,
                ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, message));
    }

    /** 핸들러가 기대한 요청 Parameter를 찾지 못한 경우 발생(RequestParam) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, MissingServletRequestParameterException e) {
        return service.response(request,
                ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, e.getParameterName()));
    }

    /** 핸들러가 기대한 요청 Parameter를 찾지 못한 경우 발생(Multipart) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, MissingServletRequestPartException e) {
        return service.response(request,
                ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, e.getRequestPartName()));
    }

    /** 핸들러가 기대한 요청 Parameter를 찾지 못한 경우 발생 **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();

        if(message.indexOf(":") > -1){
            String[] arr = message.split(":");
            if(!ObjectUtils.isEmpty(arr)){
                if(arr.length == 2) message = arr[0];
                else if(arr.length > 2) message = arr[0]+":"+arr[1];
            }
        }

        return service.response(request,
                ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, e.getName() + " => " + e.getValue() + " [" + message + "]"));
    }

    /** 유효성 검사 실패 시 발생하는 예외를 처리(RequestParam) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class) //
    protected BaseResponse handle(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException e) {
        Iterator<ConstraintViolation<?>> violationIterator = e.getConstraintViolations().iterator();
        String fieldName = null;
        Object rejectValue = null;
        String causeMessage = null;
        ResponseCode responseCode = null;

        if(violationIterator.hasNext()){
            ConstraintViolation<?> violation = violationIterator.next();
            String propertyPath = violation.getPropertyPath().toString();
            fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            rejectValue = violation.getInvalidValue();
            causeMessage = violation.getMessage();
        }

        if(causeMessage.equals(CONSTRAINT_REQUIRE_MESSAGE)) {
            responseCode = ResponseCode.FAIL_MISSING_REQUEST_PARAMETER;
            rejectValue = null;
            causeMessage = null;
        }else {
            responseCode = ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER;
        }

        return service.response(request,
                ParamValidate.build(responseCode, fieldName, rejectValue, causeMessage));
    }

    /** 유효성 검사 실패 시 발생하는 예외를 처리(RequestBody) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class) //
    protected BaseResponse handle(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        FieldError fieldError = result.getFieldError();

        String fieldName = fieldError.getField();
        Object rejectValue = fieldError.getRejectedValue();
        String causeMessage = fieldError.getDefaultMessage();
        ResponseCode responseCode = null;

        if(causeMessage.equals(CONSTRAINT_REQUIRE_MESSAGE)) {
            responseCode = ResponseCode.FAIL_MISSING_REQUEST_PARAMETER;
            rejectValue = null;
            causeMessage = null;
        }else {
            responseCode = ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER;
        }

        return service.response(request,
                ParamValidate.build(responseCode, fieldName, rejectValue, causeMessage));
    }

    /** 핸들러가 기대한 요청 Parameter에 대한 JSON conversion problem **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageConversionException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, HttpMessageConversionException e) {
        String message = e.getMessage();

        if(message.indexOf("\n") > -1){
            String[] arr = message.split("\n");
            if(!ObjectUtils.isEmpty(arr)){
                if(arr.length > 1) message = arr[0];
            }
        }

        return service.response(request,
                ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, message));
    }

    /** Parameter Validate Exception **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParamValidateException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, ParamValidateException e) {
        //400 - Bad Request, 핸들러가 기대한 요청 Parameter를 찾지 못한 경우 발생
        logger.error(e.getValidate().toString());

        //sendNotification(request, e);
        return printResponseLog(request, response, service.response(request, e.getValidate()));
    }

    /** 유통채널 상품 처리 상태 체크 **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GoodsControlException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, GoodsControlException e) {
        logger.error(e.getMessage());

        if(e.getResponseCode() == ResponseCode.FAIL_GOODS_CHANNEL_ERROR) sendNotification(request, e);
        return service.response(request, e.getResponseCode(), e.getChannelName(), e.getGoodsName());
    }

    /** 유상품 전시 상태 변경 오류(상품 미승인 상태) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GoodsExhibitNoApprovalStException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, GoodsExhibitNoApprovalStException e) {
        logger.error(e.getMessage());

        return service.response(request, ResponseCode.FAIL_GOODS_EXHIBIT_NO_APPROVAL_ST, e.getGoodsName());
    }

    /** 상품 전시 상태 변경 오류(상품 전시 대기 상태) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GoodsExhibitStWaittingException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, GoodsExhibitStWaittingException e) {
        logger.error(e.getMessage());

        return service.response(request, ResponseCode.FAIL_GOODS_EXHIBIT_ST_WAITING, e.getGoodsName());
    }

    /** 상품 정보 등록/변경 오류(가맹점 서비스 미사용 상태) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StoreServiceStException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, StoreServiceStException e) {
        logger.error(e.getMessage());

        return service.response(request, ResponseCode.FAIL_STORE_SERVICE_ST, e.getStoreName(), e.getStoreServiceSt());
    }

    /** 가맹점 서비스 미사용 변경 오류(전시 판매 상품 존재) **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductsOnDisplayException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, ProductsOnDisplayException e) {
        logger.error(e.getMessage());

        return service.response(request, ResponseCode.FAIL_PRODUCTS_ON_DISPLAY);
    }

    /** 상품권 사용/사용취소/발송/재발송 작업 중 유효하지 않는 경우 및 오류 발생 **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GiftException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, GiftException e) {
        logger.error(e.getMessage());

        return service.response(request, e.getResponseCode(), e.getOption());
    }

    /** 상품권 판매/판매취소 작업 중 유효하지 않는 경우 및 오류 발생 **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TradeException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, TradeException e) {
        logger.error(e.getMessage());

        if(StringUtils.isEmpty(e.getAddMessage())){
            return service.response(request, e.getResponseCode());
        }else{
            return service.responseAddCustomMessage(request, e.getResponseCode(), e.getAddMessage());
        }
    }

    /** API 권한 오류 **/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(APIPermissionAuthException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, APIPermissionAuthException e) {
        //401 - Unautorized, 권한 없음
        logger.error(e.getMessage());

        sendNotification(request, e);
        return printResponseLog(request, response, service.responseAddCustomMessage(request, ResponseCode.FAIL_NO_AUTH, e.getMessage()));
    }

    /** API 권한 오류(로그X) **/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(APIPermissionAuthExceptionForNoLogger.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, APIPermissionAuthExceptionForNoLogger e) {
        //401 - Unautorized, 권한 없음
        logger.error(e.getMessage());

        sendNotification(request, e);
        return service.response(request, ResponseCode.FAIL_NO_AUTH);
    }

    /** WebController Exception **/
    @ExceptionHandler(WebException.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, WebException e) { //web error
        HashMap<String, Object> attrs = e.getAttrs();

        int status = (int) attrs.get("status");
        response.setStatus(status);

        logger.error(e.getMessage(), e);


        BaseResponse res = service.response(request, ResponseCode.FAIL);

        if(e.isSendNotification()) sendNotificationWebException(request, e);
        return printResponseLog(request, response, res);
    }


    /** 모든 Exception **/
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseResponse handle(HttpServletRequest request, HttpServletResponse response, Exception e) {
        logger.error(e.getMessage(), e);

        sendNotification(request, e);

        return printResponseLog(request, response, service.response(request, ResponseCode.FAIL));
    }

    /** log print **/
    private BaseResponse printResponseLog(HttpServletRequest request, HttpServletResponse response, BaseResponse res){
        if(!response.isCommitted()){
            String resContentType = (StringUtils.isEmpty(response.getContentType()))?"":(" ["+response.getContentType()+"]");
            String servletPath = request.getServletPath();
            String ip = (String) request.getAttribute("clientIP");
            long startTime = (long) request.getAttribute("startTime");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String output = objectMapper.writeValueAsString(res);
                logger.info("<== "+output);
            }catch (Exception e){e.printStackTrace();}

            logger.info(ip+resContentType+" ("+ servletPath +":"+request.getMethod()+") "+(System.currentTimeMillis() - startTime)+"ms ===============================");
        }

        return res;
    }

    /** 알람 전송 **/
    private void sendNotification(HttpServletRequest request, Exception e){
        e.printStackTrace();
        String dateTime = null;
        try{
            dateTime = Util.timestamp2strdate((Long) request.getAttribute(AttributeKey.DATE_TIME_KEY.getKey()), "yyyy-MM-dd HH:mm:ss");
        }catch (Exception e2){e2.printStackTrace();}

        String serviceId = (String) request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        String storeId = (String) request.getHeader(AuthorizationHeaderKey.STORE_ID_KEY.getKey());
        String ipAddress = (String) request.getAttribute(AttributeKey.IP_ADDRESS_KEY.getKey());

        String path = request.getServletPath();
        String method = request.getMethod();
        String error = e.getMessage();
        String message = e.getLocalizedMessage();
        String trace = ExceptionUtils.getStackTrace(e);
        String params = (String) request.getAttribute(AttributeKey.PARAMS_KEY.getKey());

        StringBuffer sb = new StringBuffer();
        sb.append("dateTime : " + dateTime + System.lineSeparator());
        sb.append("serviceId : " + serviceId + System.lineSeparator());
        sb.append("storeId : " + storeId + System.lineSeparator());
        sb.append("ipAddress : " + ipAddress + System.lineSeparator());
        sb.append("path : " + path + ":"+ method.toLowerCase() + System.lineSeparator());
        sb.append("params : " + params + System.lineSeparator());
        sb.append("error : " + error + System.lineSeparator());
        sb.append("message : " + message + System.lineSeparator());
        //sb.append("trace : " + trace + System.lineSeparator());

        telegramService.asyncSendMessage(sb.toString());
    }

    /** 알람 전송(WebException 전용) **/
    private void sendNotificationWebException(HttpServletRequest request, WebException e){
        e.printStackTrace();
        HashMap<String, Object> attrs = e.getAttrs();
        String dateTime = null;
        try{
            dateTime = Util.timestamp2strdate((Long) attrs.get(AttributeKey.DATE_TIME_KEY.getKey()), "yyyy-MM-dd HH:mm:ss");
        }catch (Exception e2){e2.printStackTrace();}

        String serviceId = (String) attrs.get(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        String storeId = (String) attrs.get(AuthorizationHeaderKey.STORE_ID_KEY.getKey());
        String ipAddress = (String) attrs.get(AttributeKey.IP_ADDRESS_KEY.getKey());

        String path = (String) attrs.get("path");

        String error = (String) attrs.get("error");
        String message = (String) attrs.get("message");
        String trace = (String) attrs.get("trace");
        if(StringUtils.isEmpty(trace)) trace = ExceptionUtils.getStackTrace(e);

        String params = (String) request.getAttribute(AttributeKey.PARAMS_KEY.getKey());

        StringBuffer sb = new StringBuffer();
        sb.append("dateTime : " + dateTime + System.lineSeparator());
        sb.append("serviceId : " + serviceId + System.lineSeparator());
        sb.append("storeId : " + storeId + System.lineSeparator());
        sb.append("ipAddress : " + ipAddress + System.lineSeparator());
        sb.append("path : " + path + System.lineSeparator());
        sb.append("params : " + params + System.lineSeparator());
        sb.append("error : " + error + System.lineSeparator());
        sb.append("message : " + message + System.lineSeparator());
        //sb.append("trace : " + trace + System.lineSeparator());

        telegramService.asyncSendMessage(sb.toString());
    }
}
