package com.platfos.pongift.gateway.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.gateway.response.GWResponseList;
import com.platfos.pongift.validate.annotation.GWController;
import com.platfos.pongift.gateway.model.GWCategory;
import com.platfos.pongift.validate.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 유통채널 Gateway Controller (API Document 전용)
 */
@GWController
@ParamClzz(name = "01. 상품 카테고리(GW)")
public class GWCategoryController {
    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 카테고리 리스트 조회")
    @RequestMapping(path="categories", method= RequestMethod.GET)
    public GWResponseList<GWCategory> categories(HttpServletRequest request) {
        return new GWResponseList<>();
    }
}
