package com.platfos.pongift.gateway.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.gateway.response.GWResponseBase;
import com.platfos.pongift.gateway.response.GWResponseData;
import com.platfos.pongift.gateway.response.GWResponseList;
import com.platfos.pongift.validate.annotation.GWController;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 유통채널 Gateway Controller (API Document 전용)
 */
@GWController
@ParamClzz(name = "03. 거래(GW)")
public class GWTradeController {
    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "거래 리스트 조회")
    @RequestMapping(path="trade/list", method= RequestMethod.GET)
    public GWResponseList<GWTrade> trades(HttpServletRequest request) {
        return new GWResponseList<>();
    }

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "거래 상세 조회")
    @RequestMapping(path="trade", method= RequestMethod.GET)
    public GWResponseData<GWTradeDetail> trade(HttpServletRequest request
            , @RequestParam @Require @SimIndex(simIndex = SIMIndex.POM_TRADE) @Description("거래ID") String tradeId
            , @RequestParam @Description("상품 거래 ID") String productOrderId) {
        return new GWResponseData<>();
    }

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "거래 사용 처리 요청")
    @RequestMapping(path="trade/usage/request", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseBase updateTradeUsage(HttpServletRequest request, @RequestBody GWTradeUseRequest tradeUseRequest) {
        return new GWResponseBase();
    }
/*
    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "거래 사용 처리 취소 요청")
    @RequestMapping(path="trade/usage/cancel", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseBase updateTradeUsageCancel(HttpServletRequest request, @RequestBody GWTradeId tradeId) {
        return new GWResponseBase();
    }*/
}
