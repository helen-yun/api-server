package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.authorization.exception.APIPermissionAuthExceptionForNoLogger;
import com.platfos.pongift.authorization.service.APIPermissionAuthService;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.service.CIMLedgerService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.pom.model.POMTrade;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import com.platfos.pongift.data.ssm.SSMDirectSendGiftCardService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.model.GWResponseWrapper;
import com.platfos.pongift.gateway.model.GWTradeUseRequest;
import com.platfos.pongift.gateway.service.GWService;
import com.platfos.pongift.giftcard.exeption.GiftException;
import com.platfos.pongift.giftcard.model.*;
import com.platfos.pongift.giftcard.service.GiftCardService;
import com.platfos.pongift.goods.exception.GoodsExhibitNoApprovalStException;
import com.platfos.pongift.goods.model.Goods;
import com.platfos.pongift.goods.service.GoodsService;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.security.AES256Util;
import com.platfos.pongift.security.SHA256Util;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.store.service.StoreService;
import com.platfos.pongift.telegram.service.TelegramService;
import com.platfos.pongift.trade.service.TradeService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.MapConstraints;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.model.ParamValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import javax.xml.bind.ValidationException;
import java.math.BigInteger;
import java.util.*;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@ParamClzz(name = "5. 상품권")
public class GiftCardController {
    /**
     * 상품권 서비스
     **/
    private final GiftCardService giftCardService;
    /**
     * 매장 서비스
     **/
    private final StoreService storeService;
    /**
     * 상품 서비스
     **/
    private final GoodsService goodsService;
    /**
     * 거래 서비스
     **/
    private final TradeService tradeService;
    /**
     * 텔레그램 서비스
     **/
    private final TelegramService telegramService;
    /**
     * Gateway 서비스
     **/
    private final GWService gwService;
    /**
     * API 권한 서비스
     **/
    private final APIPermissionAuthService permissionAuthService;
    /**
     * 공통 응답 코드 서비스
     **/
    private final APIResponseService responseService;
    /**
     * 상품권 원장 서비스(DB)
     **/
    private final CIMLedgerService ledgerService;
    /**
     * 서비스 관리 서비스(DB)
     **/
    private final MIMServiceService mimService;
    private final SSMDirectSendGiftCardService directSendGiftCardService;

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 정보 조회")
    @RequestMapping(path = "giftcard", method = RequestMethod.GET)
    public DataResponse<GiftCardResponse> giftcard(
            HttpServletRequest request,
            @RequestParam @Require @SimIndex(simIndex = SIMIndex.MSM_STORE) @Description("매장ID") String storeId,
            @RequestParam @Require @Description("모바일상품권(바코드)번호") String barcode) throws Exception {
        /**
         * 권한 별 매장 정보 조회
         */
        Store store = storeService.getStoreByPermission(request, storeId);
        storeService.checkStoreSt(store);
        /**
         * 상품권 원장 조회
         */
        CIMLedger ledger = ledgerService.getCIMLedgerByAES256GiftNo(AES256Util.encode(barcode));
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        giftCardService.checkLedgerByUsedStore(ledger, store, agencyId);
        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId(), store.getServiceId());

        //상품권 정보 생성
        GiftCardResponse giftCardResponse = GiftCardResponse.builder().storeId(goods.getStoreId()).goodsId(goods.getGoodsId()).giftSt(ledger.getGiftSt()).tradePrice(ledger.getGiftAmt()).build();
        return responseService.data(request, ResponseCode.SUCCESS, giftCardResponse);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "SMS 템플릿 미리보기")
    @Validated(GroupB.class)
    @RequestMapping(path = "giftcard/preview", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String preview(HttpServletRequest request, @RequestParam @MapConstraints(clazz = SMSTemplate.class) Map<String, String> params) throws Exception {
        //템플릿 정보 조회
        GiftCardTemplateInfo template = giftCardService.generateGiftCard(params, false);
        if (template != null) {
            FormTp formTp = FormTp.findByCode(template.getTemplate().getFormTp());
            //html code or text code
            boolean isHtmlCd = (formTp == FormTp.SALES | formTp == FormTp.RESEND | formTp == FormTp.CHANGE_NUMBER | formTp == FormTp.GUIDE);
            return (isHtmlCd ? template.getTemplate().getHtmlCd() : template.getTemplate().getTextCd());
        }
        return null;
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "SMS 템플릿 미리보기(이미지)")
    @Validated(GroupB.class)
    @RequestMapping(path = "giftcard/preview/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] previewImage(HttpServletRequest request, @RequestParam @MapConstraints(clazz = SMSTemplate.class) Map<String, String> params) throws Exception {
        //템플릿 정보 조회
        GiftCardTemplateInfo template = giftCardService.generateGiftCard(params, true);
        if (template != null) {
            return template.getBytes();
        }
        return null;
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "SMS 템플릿 문자 발송 결과 처리(scheduler)")
    @RequestMapping(path = "giftcard/sms/result", method = RequestMethod.GET)
    public BaseResponse smsResult(HttpServletRequest request) throws Exception {
        giftCardService.asyncSendResultUpdate();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 유효기간 만료 안내 문자 발송(scheduler)")
    @RequestMapping(path = "giftcard/scheduler/guide/expiration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse guideExpirationGiftCard(HttpServletRequest request) throws Exception {
        tradeService.asyncTradeExpiredNotice();
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE, APIRole.EXTERNAL_GATEWAY_CHANNEL})
    @ParamMethodValidate(methodName = "상품권 재발송")
    @Validated({GroupA.class})
    @RequestMapping(path = "giftcard/resend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse resendGiftCard(HttpServletRequest request, @RequestBody @Valid ResendGiftCard resendGiftCard) throws Exception {
        /**
         * 재발송 상품권 조회
         */
        CIMLedger ledger = giftCardService.getLedgerByResendGiftCard(resendGiftCard);
        /**
         * 재발송 유효성 검사
         */
        giftCardService.validate(ledger, ModifyTp.RESEND);
        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId());

        //상품권 재발송
        //알림톡 발신 여부(Y/N) 요청 파라메터 추가
        tradeService.tradeResend(ledger, ModifyTp.RESEND, resendGiftCard);

        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE, APIRole.EXTERNAL_GATEWAY_CHANNEL})
    @ParamMethodValidate(methodName = "상품권 번호변경 재발송")
    @Validated({GroupB.class})
    @RequestMapping(path = "giftcard/resend/phoneno", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse resendPhoneNoGiftCard(HttpServletRequest request, @RequestBody @Valid ResendGiftCard resendGiftCard) throws Exception {
        /**
         * 재발송 상품권 조회
         */
        CIMLedger ledger = giftCardService.getLedgerByResendGiftCard(resendGiftCard);
        /**
         * 번호변경 유효성 검사
         */
        giftCardService.validate(ledger, ModifyTp.CHANGE_NUMBER);
        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId());

        //상품권 번호변경 재발송
        //알림톡 발신 여부(Y/N) 요청 파라메터 추가
        tradeService.tradeResend(ledger, resendGiftCard.getPhoneNo(), ModifyTp.CHANGE_NUMBER, resendGiftCard);
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "SMS 템플릿 문자 발송")
    @RequestMapping(path = "giftcard/sms", method = RequestMethod.POST)
    public BaseResponse sms(HttpServletRequest request, @RequestBody SMSTemplate smsTemplate) throws Exception {
        giftCardService.asyncSms(smsTemplate);
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 수기발송 서비스")
    @RequestMapping(path = "giftcard/send/{groupNo}", method = RequestMethod.POST)
    public DataResponse<Integer> sendGoodsGiftCard(HttpServletRequest request, @PathVariable("groupNo") int groupNo) {
        int sendingCounts = directSendGiftCardService.sendDirectGiftCard(groupNo, TradeTp.SALES);
        return responseService.data(request, ResponseCode.SUCCESS, sendingCounts);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 직발송 서비스")
    @RequestMapping(path = "giftcard/direct/send/{groupNo}", method = RequestMethod.POST)
    public DataResponse<Integer> sendDirectGiftCard(HttpServletRequest request, @PathVariable("groupNo") int groupNo) {
        int sendingCounts = directSendGiftCardService.sendDirectGiftCard(groupNo, TradeTp.SEND_DIRECT);
        return responseService.data(request, ResponseCode.SUCCESS, sendingCounts);
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 사용 처리")
    @RequestMapping(path = "giftcard/use", method = RequestMethod.POST)
    public DataResponse<GiftCardTradeResponse> giftcardUse(HttpServletRequest request, @RequestBody @Valid GiftCardTrade giftCardTrade) throws Exception {
        /**
         * 권한 별 매장 정보 조회
         */
        Store store = storeService.getStoreByPermission(request, giftCardTrade.getStoreId());
        storeService.checkStoreSt(store);
        /**
         * 상품권 원장 조회
         */
        CIMLedger ledger = ledgerService.getCIMLedgerByAES256GiftNo(AES256Util.encode(giftCardTrade.getBarcode()));
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        giftCardService.checkLedgerByUsedStore(ledger, store, agencyId);

        /**
         * 상품권 원장 사용 처리 유효성 검사
         */
        giftCardService.validate(ledger, ModifyTp.USE);
        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId(), store.getServiceId());

        // 거래 상세 정보 조회(판매)
        POMTradeGift tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SALES);
        if (tradeGift == null) {
            // 거래 상세 정보 조회(직발송)
            tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SEND_DIRECT);
        }

        //거래 정보 조회
        POMTrade pomTrade = tradeService.getPOMTrade(tradeGift.getTradeId());

        //GW 사용처리 시작
        String gwURL = "trade/usage/request";
        String gwMethod = "POST";
        String channelGb = mimService.getChannelGbByServiceId(pomTrade.getServiceId());
        ParamValidate gwValidate = gwService.validate(gwURL, gwMethod, channelGb);

        if (!gwValidate.hasError()) {
            GWTradeUseRequest gwTradeUseRequest = GWTradeUseRequest.builder()
                    .tradeId(pomTrade.getOrderNo())
                    .productOrderId(pomTrade.getDetailNo())
                    .ledgerId(ledger.getLedgerId())
                    .giftNo(ledger.getGiftNoAes256())
                    .build();
            HttpEntity httpEntity = gwService.createHttpEntity(gwMethod, MediaType.APPLICATION_JSON, gwTradeUseRequest);
            GWResponseWrapper<?> result = gwService.send(null, channelGb, gwURL, gwMethod, httpEntity);
            if (!result.getResponse().isSuccess()) {
                telegramService.asyncSendMessage("/giftcard/use" + System.lineSeparator() + "Gateway Error : " + result.getResponse().getMessage());
            }
        }
        //GW 사용처리 끝

        //상품권 사용 처리
        GiftCardTradeResponse giftCardTradeResponse = tradeService.tradeUse(store, ledger, giftCardTrade, goods, tradeGift, TradeTp.USE);
        return responseService.data(request, ResponseCode.SUCCESS, giftCardTradeResponse);
    }

    @APIPermission(roles = {APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 사용 취소 처리")
    @RequestMapping(path = "giftcard/use/cancel", method = RequestMethod.POST)
    public DataResponse<GiftCardTradeResponse> giftcardUseCancel(HttpServletRequest request, @RequestBody @Valid GiftCardTrade giftCardTrade) throws Exception {
        /**
         * 권한 별 매장 정보 조회
         */
        Store store = storeService.getStoreByPermission(request, giftCardTrade.getStoreId());
        /**
         * 매장 상태 확인
         */
        storeService.checkStoreSt(store);

        /**
         * 상품권 원장 조회
         */
        CIMLedger ledger = ledgerService.getCIMLedgerByAES256GiftNo(AES256Util.encode(giftCardTrade.getBarcode()));
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        giftCardService.checkLedgerByUsedStore(ledger, store, agencyId);

        //상품권 원장 사용취소 처리 유효성 검사
        giftCardService.validate(ledger, ModifyTp.USE_CANCEL);

        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId(), store.getServiceId());

        // 거래 상세 정보 조회(판매)
        POMTradeGift tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SALES);
        if (tradeGift == null) {
            // 거래 상세 정보 조회(직발송)
            tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SEND_DIRECT);
        }

        //거래 정보 조회
        POMTrade pomTrade = tradeService.getPOMTrade(tradeGift.getTradeId());

        //GW 사용 취소처리 시작
        String gwURL = "trade/cancel/request";
        String gwMethod = "POST";
        String channelGb = mimService.getChannelGbByServiceId(pomTrade.getServiceId());
        ParamValidate gwValidate = gwService.validate(gwURL, gwMethod, channelGb);

        if (!gwValidate.hasError()) {
            GWTradeUseRequest gwTradeUseRequest = GWTradeUseRequest.builder()
                    .tradeId(pomTrade.getOrderNo())
                    .productOrderId(pomTrade.getDetailNo())
                    .ledgerId(ledger.getLedgerId())
                    .giftNo(ledger.getGiftNoAes256())
                    .build();
            HttpEntity httpEntity = gwService.createHttpEntity(gwMethod, MediaType.APPLICATION_JSON, gwTradeUseRequest);
            GWResponseWrapper<?> result = gwService.send(null, channelGb, gwURL, gwMethod, httpEntity);
            if (!result.getResponse().isSuccess()) {
                telegramService.asyncSendMessage("/giftcard/use/cancel" + System.lineSeparator() + "Gateway Error : " + result.getResponse().getMessage());
            }
        }
        //GW 사용 취소처리 끝

        //상품권 사용 취소 처리
        GiftCardTradeResponse giftCardTradeResponse = tradeService.tradeUse(store, ledger, giftCardTrade, goods, tradeGift, TradeTp.USE_CANCEL);
        return responseService.data(request, ResponseCode.SUCCESS, giftCardTradeResponse);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품권 사용 취소 처리(사용 취소 기간 미체크)")
    @RequestMapping(path = "giftcard/use/cancel/uncheck", method = RequestMethod.POST)
    public DataResponse<GiftCardTradeResponse> giftcardUseCancelUncheck(HttpServletRequest request, @RequestBody @Valid GiftCardTrade giftCardTrade) throws Exception {
        /**
         * 권한 별 매장 정보 조회
         */
        Store store = storeService.getStoreByPermission(request, giftCardTrade.getStoreId());
        /**
         * 매장 상태 확인
         */
        storeService.checkStoreSt(store);

        /**
         * 상품권 원장 조회
         */
        CIMLedger ledger = ledgerService.getCIMLedgerByAES256GiftNo(AES256Util.encode(giftCardTrade.getBarcode()));
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        giftCardService.checkLedgerByUsedStore(ledger, store, agencyId);

        //상품권 원장 사용취소 처리 유효성 검사
        giftCardService.validateUncheckedPeriod(ledger, ModifyTp.USE_CANCEL);

        /**
         * 상품 or 직발송 정보 조회
         */
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        /**
         * 상품공급자 서비스 상태 확인
         */
        giftCardService.checkGoodsServiceSt(goods.getServiceId(), store.getServiceId());

        // 거래 상세 정보 조회(판매)
        POMTradeGift tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SALES);
        if (tradeGift == null) {
            // 거래 상세 정보 조회(직발송)
            tradeGift = tradeService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SEND_DIRECT);
        }

        //거래 정보 조회
        POMTrade pomTrade = tradeService.getPOMTrade(tradeGift.getTradeId());

        //GW 사용 취소처리 시작
        String gwURL = "trade/cancel/request";
        String gwMethod = "POST";
        String channelGb = mimService.getChannelGbByServiceId(pomTrade.getServiceId());
        ParamValidate gwValidate = gwService.validate(gwURL, gwMethod, channelGb);

        if (!gwValidate.hasError()) {
            GWTradeUseRequest gwTradeUseRequest = GWTradeUseRequest.builder()
                    .tradeId(pomTrade.getOrderNo())
                    .productOrderId(pomTrade.getDetailNo())
                    .ledgerId(ledger.getLedgerId())
                    .giftNo(ledger.getGiftNoAes256())
                    .build();
            HttpEntity httpEntity = gwService.createHttpEntity(gwMethod, MediaType.APPLICATION_JSON, gwTradeUseRequest);
            GWResponseWrapper<?> result = gwService.send(null, channelGb, gwURL, gwMethod, httpEntity);
            if (!result.getResponse().isSuccess()) {
                telegramService.asyncSendMessage("/giftcard/use/cancel" + System.lineSeparator() + "Gateway Error : " + result.getResponse().getMessage());
            }
        }
        //GW 사용 취소처리 끝

        //상품권 사용 취소 처리
        GiftCardTradeResponse giftCardTradeResponse = tradeService.tradeUse(store, ledger, giftCardTrade, goods, tradeGift, TradeTp.USE_CANCEL);
        return responseService.data(request, ResponseCode.SUCCESS, giftCardTradeResponse);
    }

    @APIPermission(roles = {APIRole.TICKETING})
    @ParamMethodValidate(methodName = "상품권 수기발송 서비스")
    @RequestMapping(path = "giftcard/send", method = RequestMethod.POST) // TODO: URL 정의 필요
    public BaseResponse sendAgencyGiftCard(HttpServletRequest request, @RequestBody @Valid SendGoodsRequest sendGoodsRequest) {
        // 권한 별 매장 정보 조회
        try {
            Store store = storeService.getStoreByPermission(request, sendGoodsRequest.getStoreId());
            String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
            Goods goods = goodsService.getGoods(agencyId, sendGoodsRequest.getGoodsId());
            // 매장 상태 확인
            storeService.checkStoreSt(store);
            // 해당 매장 상품인지 체크
            goodsService.checkGoodsIdAndStoreId(sendGoodsRequest.getGoodsId(), sendGoodsRequest.getStoreId());
            // 상품 승인 상태 체크?
            goodsService.checkGoodsExhibitApprovalSt(goods);
        } catch (APIPermissionAuthExceptionForNoLogger e) {
            log.error(e.getMessage());
            return responseService.response(request, ResponseCode.FAIL_NO_AUTH);
        } catch (GiftException e) {
            log.error(e.getMessage());
            return responseService.response(request, ResponseCode.FAIL_GIFT_SERVICE_ST_NOT_USED);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            return responseService.response(request, ResponseCode.FAIL_MISSING_REQUEST_PARAMETER);
        } catch (GoodsExhibitNoApprovalStException e) {
            log.error(e.getMessage());
            return responseService.response(request, ResponseCode.FAIL_GOODS_EXHIBIT_NO_APPROVAL_ST);
        }

        directSendGiftCardService.sendDirectGiftCard(sendGoodsRequest);
        return responseService.response(request, ResponseCode.SUCCESS);
    }

}