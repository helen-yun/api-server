package com.platfos.pongift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.gim.service.GIMChannelInfoService;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.model.GWGiftResponse;
import com.platfos.pongift.gateway.model.GWMultiTradeApprovedRequest;
import com.platfos.pongift.gateway.model.GWTradeApprovedRequest;
import com.platfos.pongift.gateway.model.GWTradeApprovedResponse;
import com.platfos.pongift.goods.model.Goods;
import com.platfos.pongift.goods.service.GoodsService;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.trade.exception.TradeException;
import com.platfos.pongift.trade.model.Trade;
import com.platfos.pongift.trade.service.TradeService;
import com.platfos.pongift.validate.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
@ParamClzz(name = "98. 상품 판매 승인")
public class TradeApprovedController {
    /**
     * 거래 서비스
     */

    private final TradeService tradeService;
    private final GoodsService goodsService;
    private final GIMChannelInfoService channelInfoService;
    private final MIMServiceService mimServiceService;


    /**
     * 공통 응답 코드 서비스
     */
    private final APIResponseService responseService;

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 판매 승인 요청")
    @RequestMapping(path = "trade/approved/request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> approvedSales(HttpServletRequest request, @RequestBody GWTradeApprovedRequest approvedRequest) throws Exception {
        // 유통채널 정보, 유통채널 상품 매핑 정보, 상품 정보 조회
        MIMService channelService = mimServiceService.getMIMServiceByChannelGb(approvedRequest.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(approvedRequest.getGoods().getGoodsId(), approvedRequest.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        // 상품 판매 거래 데이터 생성
        Trade trade = tradeService.createSaleTrade(approvedRequest, channelService.getServiceId(), goods, channelInfo, TradeTp.SALES);
        List<GWTradeApprovedResponse> gwTradeApprovedResponseList = tradeService.tradeSales(trade);
        ObjectMapper objectMapper = new ObjectMapper();
        String dataString = objectMapper.writeValueAsString(gwTradeApprovedResponseList);
        return responseService.data(request, ResponseCode.SUCCESS, dataString);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 다건 판매 승인 요청")
    @RequestMapping(path = "trade/approved/request/multi", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<String> approvedSalesMulti(HttpServletRequest request, @RequestBody GWMultiTradeApprovedRequest multiTradeApprovedRequest) throws Exception {
        // 유통채널 정보, 유통채널 상품 매핑 정보, 상품 정보 조회
        MIMService channelService = mimServiceService.getMIMServiceByChannelGb(multiTradeApprovedRequest.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(multiTradeApprovedRequest.getGoods().getGoodsId(), multiTradeApprovedRequest.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        // 다건 판매 처리
        List<Trade> tradeList = tradeService.createMultiTradeList(multiTradeApprovedRequest, channelService.getServiceId(), goods, channelInfo, TradeTp.SALES);
        List<GWTradeApprovedResponse> gwTradeApprovedResponseList = new ArrayList<>();
        for (Trade trade : tradeList) {
            gwTradeApprovedResponseList.add(tradeService.tradeSales(trade).get(0));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String dataString = objectMapper.writeValueAsString(gwTradeApprovedResponseList);
        return responseService.data(request, ResponseCode.SUCCESS, dataString);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "상품 판매 승인 취소 요청")
    @RequestMapping(path = "trade/approved/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse approvedCancel(HttpServletRequest request, @RequestBody GWTradeApprovedRequest approvedRequest) throws Exception {
        // 유통채널 정보, 유통채널 상품 매핑 정보, 상품 정보 조회
        MIMService channelService = mimServiceService.getMIMServiceByChannelGb(approvedRequest.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(approvedRequest.getGoods().getGoodsId(), approvedRequest.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        // 상품 취소 거래 데이터 생성
        Trade trade = tradeService.createOrderTrade(approvedRequest, channelService.getServiceId(), goods, channelInfo, TradeTp.SALES_CANCEL);

        //상품 판매 승인 취소 처리
        tradeService.tradeSalesCancel(trade, approvedRequest.getChannelGb());
        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "선물하기 승인 요청")
    @RequestMapping(path = "trade/approved/gift", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<GWGiftResponse> approvedGift(HttpServletRequest request, @RequestBody GWTradeApprovedRequest approvedRequest) throws Exception {
        //선물하기 승인
        List<GWGiftResponse> giftResponses = tradeService.tradeGift(approvedRequest);
        return responseService.list(request, ResponseCode.SUCCESS, giftResponses);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "반품 승인")
    @RequestMapping(path = "trade/approved/return", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse approvedReturn(HttpServletRequest request, @RequestBody GWTradeApprovedRequest approvedRequest) throws Exception {
        // 유통채널 정보, 유통채널 상품 매핑 정보, 상품 정보 조회
        MIMService channelService = mimServiceService.getMIMServiceByChannelGb(approvedRequest.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(approvedRequest.getGoods().getGoodsId(), approvedRequest.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        // 반품 승인 거래 데이터 생성
        Trade trade = tradeService.createOrderTrade(approvedRequest, channelService.getServiceId(), goods, channelInfo, TradeTp.RETURN);

        // 반품 승인 처리
        tradeService.tradeSalesCancel(trade, approvedRequest.getChannelGb());

        return responseService.response(request, ResponseCode.SUCCESS);
    }

    @APIPermission(roles = {APIRole.EXTERNAL_GATEWAY_CHANNEL, APIRole.EXTERNAL_SERVICE})
    @ParamMethodValidate(methodName = "반품 요청 주문")
    @RequestMapping(path = "trade/request/return", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse requestReturnList(HttpServletRequest request, @RequestBody GWTradeApprovedRequest approvedRequest) throws Exception {
        // 중복 거래 조회
        if (tradeService.isDuplicateReturnTrade(approvedRequest, TradeTp.RETURN_REQUESTED)) {
            return responseService.response(request, ResponseCode.FAIL_TRADE_REQUEST_DUPLICATE);
        }

        // 유통채널 정보, 유통채널 상품 매핑 정보, 상품 정보 조회
        MIMService channelService = mimServiceService.getMIMServiceByChannelGb(approvedRequest.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(approvedRequest.getGoods().getGoodsId(), approvedRequest.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        // 반품 요청 거래 데이터 생성
        Trade trade = tradeService.createOrderTrade(approvedRequest, channelService.getServiceId(), goods, channelInfo, TradeTp.RETURN_REQUESTED);

        // 반품 요청 처리
        tradeService.tradeReturnRequest(trade);
        return responseService.response(request, ResponseCode.SUCCESS);
    }
}
