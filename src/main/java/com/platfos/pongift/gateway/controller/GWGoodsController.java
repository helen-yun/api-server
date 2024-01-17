package com.platfos.pongift.gateway.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.response.GWResponseBase;
import com.platfos.pongift.gateway.response.GWResponseData;
import com.platfos.pongift.gateway.response.GWResponseListWithPage;
import com.platfos.pongift.validate.annotation.GWController;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 유통채널 Gateway Controller (API Document 전용)
 */
@GWController
@ParamClzz(name = "02. 상품(GW)")
public class GWGoodsController {
    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 리스트 조회")
    @RequestMapping(path="goods/list", method= RequestMethod.GET)
    public GWResponseListWithPage<GWGoodsListResponse> goodsList(HttpServletRequest request
            , @RequestParam(required = false) @Description("상품 카테고리") String categoryTp
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.goodsApprovalSt) @Description("승인 상태") String approvalSt
            , @RequestParam(required = false) @Description("상품명 키워드") String keywordGoodsNm
            , @RequestParam(required = false) @Min(1) @Description("조회 페이지") String page
            , @RequestParam(required = false) @Min(1) @Max(100) @Description("1페이지 당 조회 건수 (기본 : 20, 최대 : 100)") String pageSize
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.orderCd) @Description("정렬순서 코드") String orderCd) {
        return new GWResponseListWithPage<>();
    }

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 상세 조회")
    @RequestMapping(path="goods", method= RequestMethod.GET)
    public GWResponseData<GWGoodsResponse> goods(HttpServletRequest request, @RequestParam @Require @Description("상품ID") String goodsId) {
        return new GWResponseData();
    }

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 등록")
    @Validated(GroupA.class)
    @RequestMapping(path="goods", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseData<GWGoodsId> putGoods(HttpServletRequest request, @RequestBody GWGoodsRequest goods) {
        return new GWResponseData();
    }

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 변경")
    @Validated(GroupB.class)
    @RequestMapping(path="goods", method= RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseBase postGoods(HttpServletRequest request, @RequestBody GWGoodsRequest goods) {
        return new GWResponseBase();
    }

    /*@APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodRole = { @MethodRole(methodRoleType = MethodRoleType.DELETE) }, methodName = "상품 삭제")
    @RequestMapping(path="goods", method= RequestMethod.DELETE, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse deleteGoods(HttpServletRequest request, @RequestBody GWGoodsId goodsId) {
        return responseService.response(request, ResponseCode.SUCCESS);
    }*/

    @APIPermission(roles = { APIRole.EXTERNAL_GATEWAY_CHANNEL })
    @ParamMethodValidate(methodName = "상품 전시 상태 변경")
    @Validated(GroupA.class)
    @RequestMapping(path="goods/exhibitSt", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public GWResponseBase updateGoodsExhibitSt(HttpServletRequest request, @RequestBody GWGoodsExhibitSt goodsExhibitSt) {
        return new GWResponseBase();
    }
}
