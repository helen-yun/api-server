package com.platfos.pongift.gateway.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.gateway.response.GWResponseBase;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 유통채널 Gateway Controller (API Document 전용)
 */
@GWController
@ParamClzz(name = "04. 상품권(GW)")
public class GWGiftController {
    @Autowired
    APIResponseService responseService;

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품권 유효기간 연장")
    @RequestMapping(path="gift/extension", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseBase updateTradeUsage(HttpServletRequest request, @RequestBody GWGiftExtensionRequest gwGiftExtensionRequest) {
        return new GWResponseBase();
    }
}
