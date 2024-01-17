package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.goods.model.*;
import com.platfos.pongift.goods.service.GoodsService;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.store.service.StoreService;
import com.platfos.pongift.trade.service.TradeService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.model.ParamValidate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RequiredArgsConstructor
@RestController
@ParamClzz(name = "4. 상품")
public class GoodsController {
    /**
     * 상품 서비스
     **/
    private final GoodsService goodsService;

    /**
     * 매장 서비스
     **/
    private final StoreService storeService;

    /**
     * 서비스 정보 서비스
     */
    private final MIMServiceService mimServiceService;

    /**
     * 공통 응답 코드 서비스
     **/
    private final APIResponseService responseService;

    private final TradeService tradeService;

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY})
    @ParamMethodValidate(methodName = "상품 상세 조회")
    @RequestMapping(path = "goods", method = RequestMethod.GET)
    public DataResponse<Goods> goods(HttpServletRequest request
            , @RequestParam @Require @SimIndex(simIndex = SIMIndex.GIM_GOODS) @Description("상품ID") String goodsId) {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //상품 정보 조회
        Goods goods = goodsService.getGoods(agencyId, goodsId);
        return responseService.data(request, ResponseCode.SUCCESS, goods);
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY})
    @ParamMethodValidate(methodName = "상품 리스트 조회")
    @RequestMapping(path = "goods/list", method = RequestMethod.GET)
    public DataResponse<GoodsList> goodsList(HttpServletRequest request
            , @RequestParam(required = false) @SimIndex(simIndex = SIMIndex.MSM_STORE) @Description("매장ID") String storeId
            , @RequestParam(required = false) @Description("상품 카테고리 코드") String goodsCategoryCd
            , @RequestParam(required = false) @Description("상품명 키워드") String keywordGoodsNm
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.exhibitSt) @Description("상품 전시 상태") String exhibitSt
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.goodsApprovalSt) @Description("승인 상태") String approvalSt
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.goodsProcessSt) @Description("처리 상태") String processSt
            , @RequestParam(required = false) @Min(1) @Description("조회 페이지") Integer page
            , @RequestParam(required = false) @Min(1) @Max(100) @Description("1페이지 당 조회 건수 (기본 : 20, 최대 : 100)") Integer pageSize
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.orderCd) @Description("정렬순서 코드") String orderCd) {

        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        //검색 조건
        GoodsSearchCondition condition = GoodsSearchCondition.builder()
                .storeId(storeId)
                .agencyId(agencyId)
                .goodsCategoryCd(goodsCategoryCd)
                .keywordGoodsNm(keywordGoodsNm)
                .exhibitSt(exhibitSt)
                .approvalSt(approvalSt)
                .processSt(processSt)
                .page(page)
                .pageSize(pageSize)
                .orderCd(orderCd)
                .build();

        //상품 리스트 조회
        return responseService.data(request, ResponseCode.SUCCESS, goodsService.getGoodsList(condition));
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 내용 이미지 미리보기")
    @RequestMapping(path = "goods/preview", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String goodsPreview(HttpServletRequest request
            , @RequestParam @Require @SimIndex(simIndex = SIMIndex.GIM_GOODS) @Description("상품ID") String goodsId) throws Exception {
        //상품 정보 조회
        Goods goods = goodsService.getGoods(goodsId);
        //매장 정보 조회
        Store store = storeService.getStore(goods.getStoreId());
        // 서비스 정보 조회
        MIMService mimService = mimServiceService.getMIMService(goods.getServiceId());
        //상품 내용 이미지 미리보기(Html)
        return goodsService.html(store, goods, mimService);
    }

    @APIPermission(roles = {APIRole.ANONYMOUS})
    @ParamMethodValidate(methodName = "상품 내용 이미지 미리보기(이미지)")
    @RequestMapping(path = "goods/preview/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] goodsPreviewImage(HttpServletRequest request
            , @RequestParam @Require @SimIndex(simIndex = SIMIndex.GIM_GOODS) @Description("상품ID") String goodsId) throws Exception {
        //상품 정보 조회
        Goods goods = goodsService.getGoods(goodsId);
        //매장 정보 조회
        Store store = storeService.getStore(goods.getStoreId());
        // 서비스 정보 조회
        MIMService mimService = mimServiceService.getMIMService(goods.getServiceId());

        //상품 내용 이미지 미리보기(이미지)
        return goodsService.getTemplateBytes(store, goods, mimService);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 정보 유통채널 반영 작업(scheduler)")
    @RequestMapping(path = "goods/scheduler", method = RequestMethod.GET)
    public BaseResponse goodsScheduler(HttpServletRequest request) {
        goodsService.asyncGoodsScheduler();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "전시 판매 만료일 연장 작업(scheduler)")
    @RequestMapping(path = "goods/scheduler/expiration/extension", method = RequestMethod.GET)
    public BaseResponse extensionExpirationExhibitGoods(HttpServletRequest request) {
        goodsService.asyncExtensionExpirationExhibitGoods();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY})
    @ParamMethodValidate(methodName = "상품 등록")
    @Validated(GroupA.class)
    @RequestMapping(path = "goods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<GoodsId> putGoods(HttpServletRequest request, @RequestBody @Valid Goods goods) throws Exception {
        //상품 등록 유효성 검사
        ParamValidate validate = goodsService.validate(goods);
        if (validate.hasError()) return responseService.data(request, validate);

        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //매장 정보 조회
        Store store = storeService.getStore(agencyId, goods.getStoreId());
        if (ObjectUtils.isEmpty(store)) {
            throw new APIPermissionAuthException("No API Permission Authorization : goods/insert => agencyId : " + agencyId + ", storeId : " + goods.getStoreId());
        }

        //등록 할 상품의 서비스 아이디 설정
        goods.setServiceId(store.getServiceId());

        //매장 서비스 상태 확인
        goodsService.checkStoreServiceSt(store);

        //상품 등록
        String newGoodsId = goodsService.insertGoods(goods);

        return responseService.data(request, (StringUtils.isEmpty(newGoodsId)) ? ResponseCode.FAIL : ResponseCode.SUCCESS, GoodsId.builder().goodsId(newGoodsId).build());
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY})
    @ParamMethodValidate(methodName = "상품 수정")
    @Validated(GroupB.class)
    @RequestMapping(path = "goods", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse postGoods(HttpServletRequest request, @RequestBody @Valid Goods goods) throws Exception {
        //상품 수정 유효성 검사
        ParamValidate validate = goodsService.validate(goods);
        if (validate.hasError()) return responseService.response(request, validate);

        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        //상품 정보 조회
        Goods dbGoods = goodsService.getGoods(agencyId, goods.getGoodsId());
        if (ObjectUtils.isEmpty(dbGoods))
            throw new APIPermissionAuthException("No API Permission Authorization : goods/updateExhibitSt => agencyId : " + agencyId + ", goodsId : " + goods.getGoodsId());

        //매장 정보 조회
        Store store = storeService.getStore(agencyId, goods.getStoreId());
        if (ObjectUtils.isEmpty(store)) {
            throw new APIPermissionAuthException("No API Permission Authorization : goods/update => agencyId : " + agencyId + ", storeId : " + goods.getStoreId());
        }

        //수정 할 상품의 서비스 아이디 설정
        goods.setServiceId(store.getServiceId());


        //매장 서비스 상태 확인
        goodsService.checkStoreServiceSt(store);
        //상품 처리 상태 확인(유통채널 작업 상태)
        goodsService.checkGoodsProcessSt(goods);

        //상품 수정
        int rs = goodsService.updateGoods(goods, false);

        return responseService.response(request, (rs <= 0) ? ResponseCode.FAIL : ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "승인된 모든 상품 내용 이미지 생성(비동기)")
    @RequestMapping(path = "goods/preview/approved/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse goodsPreviewApprovedSave(HttpServletRequest request) throws Exception {
        goodsService.asyncApprovedSaveGoodsPreview();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 내용 이미지 생성(비동기)")
    @RequestMapping(path = "goods/preview/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse goodsPreviewSave(HttpServletRequest request, @RequestBody @Valid GoodsId goodsId) throws Exception {
        Goods goods = goodsService.getGoods(goodsId.getGoodsId());
        Store store = storeService.getStore(goods.getStoreId());
        MIMService mimService = mimServiceService.getMIMService(goods.getServiceId());

        goodsService.asyncSaveGoodsPreview(store, goods, mimService);

        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY})
    @ParamMethodValidate(methodName = "상품 전시 상태 변경")
    @RequestMapping(path = "goods/updateExhibitSt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateGoodsExhibitSt(HttpServletRequest request, @RequestBody @Valid GoodsExhibitSt goodsExhibitSt) throws Exception {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        //상품 정보 조회
        Goods goods = goodsService.getGoods(agencyId, goodsExhibitSt.getGoodsId());
        if (ObjectUtils.isEmpty(goods))
            throw new APIPermissionAuthException("No API Permission Authorization : goods/updateExhibitSt => agencyId : " + agencyId + ", goodsId : " + goodsExhibitSt.getGoodsId());

        //매장 정보 조회
        Store store = storeService.getStore(agencyId, goods.getStoreId());
        if (ObjectUtils.isEmpty(store))
            throw new APIPermissionAuthException("No API Permission Authorization : goods/updateExhibitSt => agencyId : " + agencyId + ", storeId : " + goods.getStoreId());

        //전시 상태 변경 할 상품의 서비스 아이디 설정
        goods.setServiceId(store.getServiceId());

        //매장 서비스 상태 확인
        goodsService.checkStoreServiceSt(store);
        //상품 승인 상태 체크(승인)
        goodsService.checkGoodsExhibitApprovalSt(goods);
        //상품 전시 상태 체크(전시 대기)
        goodsService.checkGoodsExhibitStWaitting(goods);
        //상품 처리 상태 확인(유통채널 작업 상태)
        goodsService.checkGoodsProcessSt(goods);

        //전시 상태 변경
        goods.setExhibitSt(goodsExhibitSt.getExhibitSt());
        int rs = goodsService.updateGoodsExhibitSt(goods);

        return responseService.response(request, (rs > 0) ? ResponseCode.SUCCESS : ResponseCode.FAIL);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "유통채널 상품 품절 처리")
    @PostMapping(path = "goods/{channelGb}/{channelGoodsId}/soldOut", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse setChannelSoldOut(HttpServletRequest request, @PathVariable("channelGb") String channelGb, @PathVariable("channelGoodsId") String channelGoodsId) {
        tradeService.setChannelSoldOut(channelGb, channelGoodsId);

        return responseService.response(request, ResponseCode.SUCCESS);
    }
}
