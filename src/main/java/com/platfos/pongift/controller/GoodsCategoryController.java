package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.category.model.GoodsCategory;
import com.platfos.pongift.category.service.GoodsCategoryService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@ParamClzz(name = "3. 상품 카테고리")
public class GoodsCategoryController {
    /** 상품(전시)카테고리 서비스 **/
    @Autowired
    GoodsCategoryService service;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "상품 카테고리 리스트 조회")
    @RequestMapping(path="categories", method= RequestMethod.GET)
    public ListResponse<GoodsCategory> categories(HttpServletRequest request) {
        return responseService.list(request, ResponseCode.SUCCESS, service.getGoodsCategoryList("Y"));
    }
}
