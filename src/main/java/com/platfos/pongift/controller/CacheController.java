package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.cache.service.CacheService;
import com.platfos.pongift.data.aim.service.AIMMessageInfoService;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMReplyCodeService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.definition.ResponseCode;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.ParamClzz;
import com.platfos.pongift.validate.annotation.ParamMethodValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@ParamClzz(name = "97. 캐시 제어")
public class CacheController {
    /** 기초 코드 서비스(DB) **/
    @Autowired
    SIMCodeService simCodeService;

    /** 메시지 리소스 서비스(DB) **/
    @Autowired
    AIMMessageInfoService aimMessageInfoService;

    /** 공통 응답 코드 서비스(DB) **/
    @Autowired
    SIMReplyCodeService simReplyCodeService;

    /** 시스템 정보 서비스(DB) **/
    @Autowired
    SIMSystemInfoService simSystemInfoService;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    /** 캐시 서비스 **/
    @Autowired
    CacheService service;

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "기초 코드 캐시 재생성 (L4)")
    @RequestMapping(path = "cache/code/refresh", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse codeRefresh(HttpServletRequest request) {
        simCodeService.refreshSIMCodeListCache();
        service.asyncCallOtherServerRefreshCache(request.getServletPath());
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "기초 코드 캐시 재생성")
    @RequestMapping(path = "cache/code/refresh/me", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse codeRefreshMe(HttpServletRequest request) {
        simCodeService.refreshSIMCodeListCache();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "메시지(다국어) 캐시 재생성 (L4)")
    @RequestMapping(path = "cache/message/refresh", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse messageRefresh(HttpServletRequest request) {
        aimMessageInfoService.refreshAIMMessageInfoListCache();
        service.asyncCallOtherServerRefreshCache(request.getServletPath());
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "메시지(다국어) 캐시 재생성")
    @RequestMapping(path = "cache/message/refresh/me", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse messageRefreshMe(HttpServletRequest request) {
        aimMessageInfoService.refreshAIMMessageInfoListCache();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "응답코드 캐시 재생성 (L4)")
    @RequestMapping(path = "cache/replycode/refresh", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse replyCodeRefresh(HttpServletRequest request) {
        simReplyCodeService.refreshSIMReplyCodeCache();
        service.asyncCallOtherServerRefreshCache(request.getServletPath());
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "응답코드 캐시 재생성")
    @RequestMapping(path = "cache/replycode/refresh/me", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse replyCodeRefreshMe(HttpServletRequest request) {
        simReplyCodeService.refreshSIMReplyCodeCache();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "시스템 정보 캐시 재생성 (L4)")
    @RequestMapping(path = "cache/system/refresh", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse systemRefresh(HttpServletRequest request) {
        simSystemInfoService.refreshSIMSystemInfoCache();
        service.asyncCallOtherServerRefreshCache(request.getServletPath());
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "시스템 정보 캐시 재생성")
    @RequestMapping(path = "cache/system/refresh/me", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse systemRefreshMe(HttpServletRequest request) {
        simSystemInfoService.refreshSIMSystemInfoCache();
        return responseService.response(request, ResponseCode.SUCCESS);
    }
}
