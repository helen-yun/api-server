package com.platfos.pongift.response.service;

import com.platfos.pongift.data.aim.service.AIMMessageInfoService;
import com.platfos.pongift.data.sim.model.SIMReplyCode;
import com.platfos.pongift.data.sim.service.SIMReplyCodeService;
import com.platfos.pongift.definition.AuthorizationHeaderKey;
import com.platfos.pongift.definition.ResponseCode;
import com.platfos.pongift.goods.model.Page;
import com.platfos.pongift.response.model.*;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.validate.model.ParamValidate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;

@Service
public class APIResponseService {
    private static final Logger logger = LoggerFactory.getLogger(APIResponseService.class);

    @Autowired
    AIMMessageInfoService aimMessageInfoService;

    @Autowired
    SIMReplyCodeService simReplyCodeService;

    public <T> DataResponse<T> data(HttpServletRequest request, ParamValidate validate){
        DataResponse<T> result = new DataResponse<>();
        result.setResponse(createResponse(request, validate));
        result.setData(null);
        return result;
    }
    public <T> DataResponse<T> data(HttpServletRequest request, T data) {
        return data(request, ResponseCode.SUCCESS, null, data);
    }
    public <T> DataResponse<T> data(HttpServletRequest request, ResponseCode code, T data) {
        return data(request, code, null, data);
    }
    public <T> DataResponse<T> data(HttpServletRequest request, ResponseCode code, String message, T data) {
        DataResponse<T> result = new DataResponse<>();
        if(StringUtils.isEmpty(message)) result.setResponse(createResponse(request, code));
        else result.setResponse(createResponseCustomMessage(request, code, message));
        result.setData(data);
        return result;
    }
    public <T> ListResponseWithPage<T> listWithPage(HttpServletRequest request, Page page, List<T> list) {
        return listWithPage(request, ResponseCode.SUCCESS, page, list);
    }
    public <T> ListResponseWithPage<T> listWithPage(HttpServletRequest request, ParamValidate validate){
        ListResponseWithPage<T> result = new ListResponseWithPage<>();
        result.setResponse(createResponse(request, validate));
        result.setPage(null);
        result.setList(null);
        return result;
    }
    public <T> ListResponseWithPage<T> listWithPage(HttpServletRequest request, ResponseCode code){
        ListResponseWithPage<T> result = new ListResponseWithPage<>();
        result.setResponse(createResponse(request, code));
        result.setPage(null);
        result.setList(null);
        return result;
    }
    public <T> ListResponseWithPage<T> listWithPage(HttpServletRequest request, ResponseCode code, Page page, List<T> list){
        ListResponseWithPage<T> result = new ListResponseWithPage<>();
        result.setResponse(createResponse(request, code));
        result.setPage(page);
        result.setList(list);
        return result;
    }
    public <T> ListResponseWithPage<T> listWithPage(HttpServletRequest request, ResponseCode code, String message, Page page, List<T> list) {
        ListResponseWithPage<T> result = new ListResponseWithPage<>();
        if(StringUtils.isEmpty(message)) result.setResponse(createResponse(request, code));
        else result.setResponse(createResponseCustomMessage(request, code, message));
        result.setPage(page);
        result.setList(list);
        return result;
    }
    public <T> ListResponse<T> list(HttpServletRequest request, ParamValidate validate){
        ListResponse<T> result = new ListResponse<>();
        result.setResponse(createResponse(request, validate));
        result.setList(null);
        return result;
    }
    public <T> ListResponse<T> list(HttpServletRequest request, List<T> list) {
        return list(request, ResponseCode.SUCCESS, null, list);
    }
    public <T> ListResponse<T> list(HttpServletRequest request, ResponseCode code, List<T> list) {
        return list(request, code, null, list);
    }
    public <T> ListResponse<T> list(HttpServletRequest request, ResponseCode code, String message, List<T> list) {
        ListResponse<T> result = new ListResponse<>();
        if(StringUtils.isEmpty(message)) result.setResponse(createResponse(request, code));
        else result.setResponse(createResponseCustomMessage(request, code, message));
        result.setList(list);
        return result;
    }

    public BaseResponse response(HttpServletRequest request, ResponseCode code, String...vals){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponse(createResponse(request, code, vals));
        return baseResponse;
    }
    public BaseResponse response(HttpServletRequest request, ResponseCode code){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponse(createResponse(request, code));
        return baseResponse;
    }
    public BaseResponse response(HttpServletRequest request, ParamValidate validate){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponse(createResponse(request, validate));
        return baseResponse;
    }
    public BaseResponse responseAddCustomMessage(HttpServletRequest request, ResponseCode code, String addMessage){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponse(createResponseAddCustomMessage(request, code, addMessage));
        return baseResponse;
    }


    public Response createResponse(HttpServletRequest request, ResponseCode code){
        return createResponse(request, code, null);
    }

    private Response createResponse(HttpServletRequest request, ResponseCode code, String...vals){
        String codeMessageKey = code.getMessageKey();
        String lang = request.getLocale().getLanguage();

        String message = aimMessageInfoService.getMessage(codeMessageKey, lang);
        if(vals != null && vals.length > 0){
            message = String.format(message, vals);
        }

        return createResponseCustomMessage(request, code, message);
    }

    private Response createResponse(HttpServletRequest request, ParamValidate validate){
        String serviceId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        ResponseCode code = validate.getCode();
        String filedName = validate.getFiledName();

        String rejectValue = "";
        try{
            rejectValue = (validate.getRejectValue() != null) ? String.valueOf(validate.getRejectValue()) : "null";
        }catch (Exception e){e.printStackTrace();}

        if(rejectValue.getBytes().length > 100) rejectValue = Util.utf8Cut(rejectValue, 100) + "...";
        String causeMessage = (!StringUtils.isEmpty(validate.getCauseMessage())) ? validate.getCauseMessage() : null;

        String vals = "";
        if(!StringUtils.isEmpty(filedName)) vals += filedName;
        if(!StringUtils.isEmpty(causeMessage)) vals += (" =>"+((!StringUtils.isBlank(rejectValue))?(" "+rejectValue):"")+" ["+causeMessage+"]");

        String codeMessageKey = code.getMessageKey();
        String lang = request.getLocale().getLanguage();

        String message = aimMessageInfoService.getMessage(codeMessageKey, lang);
        if(!StringUtils.isEmpty(vals)) message = String.format(message, vals);

        HashMap<String, SIMReplyCode> replyCodeResources = simReplyCodeService.getReplyCodeResources();
        SIMReplyCode replyCodeResource = (replyCodeResources != null && replyCodeResources.containsKey(codeMessageKey))?replyCodeResources.get(codeMessageKey):null;
        String replyCd = (replyCodeResource != null && !StringUtils.isEmpty(replyCodeResource.getReplyCd()))?replyCodeResource.getReplyCd():null;

        return Response.builder().serviceId(serviceId).code(replyCd).message(message).build();
    }

    private Response createResponseCustomMessage(HttpServletRequest request, ResponseCode code, String message){
        String serviceId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        String codeMessageKey = code.getMessageKey();

        HashMap<String, SIMReplyCode> replyCodeResources = simReplyCodeService.getReplyCodeResources();
        SIMReplyCode replyCodeResource = (replyCodeResources != null && replyCodeResources.containsKey(codeMessageKey))?replyCodeResources.get(codeMessageKey):null;
        String replyCd = (replyCodeResource != null && !StringUtils.isEmpty(replyCodeResource.getReplyCd()))?replyCodeResource.getReplyCd():null;

        return Response.builder().serviceId(serviceId).code(replyCd).message(message).build();
    }

    private Response createResponseAddCustomMessage(HttpServletRequest request, ResponseCode code, String addMessage){
        String codeMessageKey = code.getMessageKey();
        String lang = request.getLocale().getLanguage();

        String message = aimMessageInfoService.getMessage(codeMessageKey, lang);
        if(!StringUtils.isEmpty(addMessage)) message += " (" + addMessage + ")";

        return createResponseCustomMessage(request, code, message);
    }
}
