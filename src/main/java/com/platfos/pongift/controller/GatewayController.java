package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.service.CIMLedgerService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.pom.model.POMTrade;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import com.platfos.pongift.data.pom.service.POMTradeGiftService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.gateway.service.GWService;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.model.ListResponseWithPage;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.security.SHA256Util;
import com.platfos.pongift.trade.service.TradeService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.model.ParamValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@ParamClzz(name = "98. 유통채널 게이트웨이")
public class GatewayController {
    /**
     * Gateway 서비스
     **/
    private final GWService gwService;
    /**
     * 거래 서비스
     **/
    private final TradeService tradeService;
    /**
     * 상품권 원장 서비스
     */
    private final CIMLedgerService ledgerService;
    /**
     * 거래 상세 서비스
     */
    private final POMTradeGiftService pomTradeGiftService;
    /**
     * 서비스 관리 서비스(DB)
     **/
    private final MIMServiceService mimServiceService;
    /**
     * 공통 응답 코드 서비스
     **/
    private final APIResponseService responseService;

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 카테고리 리스트 조회 (유통채널)")
    @RequestMapping(path = "gateway/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<GWCategory> categories(
            HttpServletRequest request,
            @RequestParam @SimCode(groupCode = SIMCodeGroup.channelGb) @Description("유통 채널 구분") String channelGb) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.list(request, validate);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, null);
        GWResponseWrapper<GWCategory> result = gwService.send(GWCategory.class, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.list(request, result.getList());
        }
        return responseService.list(request, ResponseCode.FAIL, result.getResponse().getMessage(), null);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 리스트 조회 (유통채널)")
    @RequestMapping(path = "gateway/goods/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponseWithPage<GWGoodsListResponse> goodsList(
            HttpServletRequest request
            , @RequestParam @SimCode(groupCode = SIMCodeGroup.channelGb) @Description("유통 채널 구분") String channelGb
            , @RequestParam(required = false) @Description("서비스 카테고리") String categoryTp
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.goodsApprovalSt) @Description("승인 상태") String approvalSt
            , @RequestParam(required = false) @Description("상품명 키워드") String keywordGoodsNm
            , @RequestParam(required = false) @Min(1) @Description("조회 페이지") String page
            , @RequestParam(required = false) @Min(1) @Max(100) @Description("1페이지 당 조회 건수 (기본 : 20, 최대 : 100)") String pageSize
            , @RequestParam(required = false) @SimCode(groupCode = SIMCodeGroup.orderCd) @Description("정렬순서 코드") String orderCd) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.listWithPage(request, validate);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, GWGoodsCondition.builder()
                .categoryTp(categoryTp)
                .approvalSt(approvalSt)
                .keywordGoodsNm(keywordGoodsNm)
                .page(page)
                .pageSize(pageSize)
                .orderCd(orderCd)
                .build());
        GWResponseWrapper<GWGoodsListResponse> result = gwService.send(GWGoodsListResponse.class, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.listWithPage(request, result.getPage(), result.getList());
        }
        return responseService.listWithPage(request, ResponseCode.FAIL, result.getResponse().getMessage(), null, null);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 조회 (유통채널)")
    @RequestMapping(path = "gateway/goods", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<GWGoodsResponse> goods(HttpServletRequest request
            , @RequestParam @SimCode(groupCode = SIMCodeGroup.channelGb) @Description("유통 채널 구분") String channelGb
            , @RequestParam @Description("상품ID (유통채널)") String goodsId) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.data(request, validate);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, null, GWGoodsId.builder().goodsId(goodsId).build());
        GWResponseWrapper<GWGoodsResponse> result = gwService.send(GWGoodsResponse.class, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.data(request, result.getData());
        }
        return responseService.data(request, ResponseCode.FAIL, result.getResponse().getMessage(), null);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 등록 (유통채널)")
    @Validated(GroupA.class)
    @RequestMapping(path = "gateway/goods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse goodsInsert(HttpServletRequest request, @RequestBody @Valid GWGoodsRequest goods) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, goods.getChannelGb());
        if (validate.hasError()) return responseService.data(request, validate);

        //상품 전시 상태 설정(유통채널 측은 '전시 대기' 상태 없음)
        String exhibitSt = goods.getExhibitSt();
        ExhibitSt goodsExhibitSt = ExhibitSt.findByCode(exhibitSt);
        if (goodsExhibitSt == null) goodsExhibitSt = ExhibitSt.STOP_SELLING;
        else if (goodsExhibitSt == ExhibitSt.WAITING_FOR_SALES) goodsExhibitSt = ExhibitSt.STOP_SELLING;
        exhibitSt = goodsExhibitSt.getCode();
        goods.setExhibitSt(exhibitSt);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, goods);
        GWResponseWrapper<GWGoodsId> result = gwService.send(GWGoodsId.class, goods.getChannelGb(), request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.data(request, result.getData());
        }

        return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, result.getResponse().getMessage());
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 변경 (유통채널)")
    @Validated(GroupB.class)
    @RequestMapping(path = "gateway/goods", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse goodsUpdate(HttpServletRequest request, @RequestBody @Valid GWGoodsRequest goods) throws Exception {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, goods.getChannelGb());
        if (validate.hasError()) return responseService.data(request, validate);

        //상품 전시 상태 설정(유통채널 측은 '전시 대기' 상태 없음)
        String exhibitSt = goods.getExhibitSt();
        ExhibitSt goodsExhibitSt = ExhibitSt.findByCode(exhibitSt);
        if (goodsExhibitSt == null) goodsExhibitSt = ExhibitSt.STOP_SELLING;
        else if (goodsExhibitSt == ExhibitSt.WAITING_FOR_SALES) goodsExhibitSt = ExhibitSt.STOP_SELLING;
        exhibitSt = goodsExhibitSt.getCode();
        goods.setExhibitSt(exhibitSt);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, goods);
        GWResponseWrapper<?> result = gwService.send(null, goods.getChannelGb(), request, httpEntity);

        if (result.getResponse().isSuccess()) {
            return responseService.response(request, ResponseCode.SUCCESS);
        }

        return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, result.getResponse().getMessage());
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 전시 상태 변경 (유통채널)")
    @Validated(GroupA.class)
    @RequestMapping(path = "gateway/goods/exhibitSt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse goodsUpdateExhibitSt(HttpServletRequest request, @RequestBody @Valid GWGoodsExhibitSt goodsExhibitSt) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, goodsExhibitSt.getChannelGb());
        if (validate.hasError()) return responseService.data(request, validate);

        //상품 전시 상태 설정(유통채널 측은 '전시 대기' 상태 없음)
        String exhibitSt = goodsExhibitSt.getExhibitSt();
        ExhibitSt gimExhibitSt = ExhibitSt.findByCode(exhibitSt);
        if (gimExhibitSt == null) gimExhibitSt = ExhibitSt.STOP_SELLING;
        else if (gimExhibitSt == ExhibitSt.WAITING_FOR_SALES) gimExhibitSt = ExhibitSt.STOP_SELLING;
        exhibitSt = gimExhibitSt.getCode();
        goodsExhibitSt.setExhibitSt(exhibitSt);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, goodsExhibitSt);
        GWResponseWrapper<?> result = gwService.send(null, goodsExhibitSt.getChannelGb(), request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.response(request, ResponseCode.SUCCESS);
        }

        return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, result.getResponse().getMessage());
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "거래 리스트 조회 (유통채널)")
    @RequestMapping(path = "gateway/trade/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<GWTrade> tradeList(HttpServletRequest request
            , @RequestParam @SimCode(groupCode = SIMCodeGroup.channelGb) @Description("유통 채널 구분") String channelGb) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.list(request, validate);

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, null, null);
        GWResponseWrapper<GWTrade> result = gwService.send(GWTrade.class, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.list(request, result.getList());
        }
        return responseService.list(request, ResponseCode.FAIL, result.getResponse().getMessage(), null);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "거래 조회 (유통채널)")
    @RequestMapping(path = "gateway/trade", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<GWTradeDetail> trade(HttpServletRequest request
            , @RequestParam @SimCode(groupCode = SIMCodeGroup.channelGb) @Description("유통 채널 구분") String channelGb
            , @RequestParam @Description("거래 주문 번호") String orderNo
            , @RequestParam(required = false) @Description("거래 상세 주문 번호") String detailNo) {
        //Gateway validate check
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.data(request, validate);

        //Connect Gateway
        GWTradeDetailRequest gwTradeDetailRequest = GWTradeDetailRequest.builder().tradeId(orderNo).productOrderId(detailNo).build();

        HttpEntity httpEntity = gwService.createHttpEntity(request, null, gwTradeDetailRequest);
        GWResponseWrapper<GWTradeDetail> result = gwService.send(GWTradeDetail.class, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.data(request, result.getData());
        }
        return responseService.data(request, ResponseCode.FAIL, result.getResponse().getMessage(), null);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 유효기간 연장(유통채널:선물하기 전용)")
    @RequestMapping(path = "gateway/gift/extension", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse giftExtension(HttpServletRequest request, @RequestBody @Valid GWGiftExtension giftExtension) {
        //거래 정보 조회
        POMTradeGift tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(giftExtension.getLedgerId(), TradeTp.SALES);
        POMTrade pomTrade = tradeService.getPOMTrade(tradeGift.getTradeId());

        //Gateway validate check
        String channelGb = mimServiceService.getChannelGbByServiceId(pomTrade.getServiceId());
        ParamValidate validate = gwService.validate(request, channelGb);
        if (validate.hasError()) return responseService.data(request, validate);

        // 상품권 조회
        CIMLedger ledger = ledgerService.getCIMLedger(giftExtension.getLedgerId());


        //Gateway Request Parameter 생성
        GWGiftExtensionRequest gwGiftExtensionRequest = GWGiftExtensionRequest.builder()
                .tradeId(pomTrade.getOrderNo())
                .productOrderId(pomTrade.getDetailNo())
                .ledgerId(giftExtension.getLedgerId())
                .expiryDate(giftExtension.getExpiryDate())
                .giftNo(ledger.getGiftNoAes256())
                .build();

        //Connect Gateway
        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, gwGiftExtensionRequest);
        GWResponseWrapper<?> result = gwService.send(null, channelGb, request, httpEntity);
        if (result.getResponse().isSuccess()) {
            return responseService.response(request, ResponseCode.SUCCESS);
        }

        return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, result.getResponse().getMessage());
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE, APIRole.PRODUCT_SUPPLIER})
    @ParamMethodValidate(methodName = "반품 요청 건 승인 처리 요청")
    @RequestMapping(path = "gateway/trade/approve/return", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse requestReturnRequestTrade(HttpServletRequest request, @RequestBody POMTradeGift pomTradeGift) {
        if (pomTradeGift.getProductId() == null) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "parameter를 확인해 주세요. (productId)");
        }

        pomTradeGift = pomTradeGiftService.selectPOMTradeGift(pomTradeGift);
        POMTrade pomTrade = tradeService.getPOMTrade(pomTradeGift.getTradeId());

        if (pomTrade == null) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "tradeId에 맞는 거래내역이 존재하지 않습니다.");
        } else if (!pomTrade.getTradeTp().equals(TradeTp.RETURN_REQUESTED.getCode())) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "거래 유형을 다시 확인해 주세요.");
        }

        String channelGb = mimServiceService.getChannelGbByServiceId(pomTrade.getServiceId());
        GWTradeDetailRequest gwTradeDetailRequest = GWTradeDetailRequest.builder()
                .tradeId(pomTrade.getOrderNo())
                .productOrderId(pomTrade.getDetailNo())
                .build();

        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, gwTradeDetailRequest);
        GWResponseWrapper<GWTradeDetail> result = gwService.send(GWTradeDetail.class, channelGb, "/trade/request/return", "POST", httpEntity);
        return responseService.data(request, result.getResponse());
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE, APIRole.PRODUCT_SUPPLIER})
    @ParamMethodValidate(methodName = "반품 요청 건 거부 처리 요청")
    @RequestMapping(path = "gateway/trade/reject/return", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse requestReturnRejectTrade(HttpServletRequest request, @RequestBody POMTradeGift pomTradeGift) {
        if (pomTradeGift.getProductId() == null) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "parameter를 확인해 주세요. (productId)");
        }
        if (pomTradeGift.getProcessDesc() == null) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "parameter를 확인해 주세요. (processDesc)");
        }
        String rejectReturnReason = pomTradeGift.getProcessDesc();

        pomTradeGift = pomTradeGiftService.selectPOMTradeGift(pomTradeGift);
        POMTrade pomTrade = tradeService.getPOMTrade(pomTradeGift.getTradeId());

        if (pomTrade == null) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "tradeId에 맞는 거래내역이 존재하지 않습니다.");
        } else if (!pomTrade.getTradeTp().equals(TradeTp.RETURN_REQUESTED.getCode())) {
            return responseService.responseAddCustomMessage(request, ResponseCode.FAIL, "거래 유형을 다시 확인해 주세요.");
        }

        String channelGb = mimServiceService.getChannelGbByServiceId(pomTrade.getServiceId());
        GWTradeDetailRequest gwTradeDetailRequest = GWTradeDetailRequest.builder()
                .tradeId(pomTrade.getOrderNo())
                .productOrderId(pomTrade.getDetailNo())
                .rejectReturnReason(rejectReturnReason)
                .build();

        HttpEntity httpEntity = gwService.createHttpEntity(request, MediaType.APPLICATION_JSON, gwTradeDetailRequest);
        GWResponseWrapper<GWTradeDetail> result = gwService.send(GWTradeDetail.class, channelGb, "/claim/reject/return", "POST", httpEntity);
        return responseService.data(request, result.getResponse());
    }
}
