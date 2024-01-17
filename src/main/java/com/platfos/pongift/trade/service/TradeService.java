package com.platfos.pongift.trade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.model.CIMLedgerHis;
import com.platfos.pongift.data.cim.service.CIMLedgerService;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.gim.service.GIMChannelInfoService;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.pom.model.*;
import com.platfos.pongift.data.pom.service.POMCustomerInfoService;
import com.platfos.pongift.data.pom.service.POMDispatchInfoService;
import com.platfos.pongift.data.pom.service.POMTradeGiftService;
import com.platfos.pongift.data.pom.service.POMTradeService;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.model.SIMPolicyInfo;
import com.platfos.pongift.data.sim.model.SIMTemplateInfo;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMPolicyInfoService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.data.sim.service.SIMTemplateInfoService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.gateway.service.GWService;
import com.platfos.pongift.giftcard.exeption.GiftException;
import com.platfos.pongift.giftcard.model.GiftCardTrade;
import com.platfos.pongift.giftcard.model.GiftCardTradeResponse;
import com.platfos.pongift.giftcard.model.ResendGiftCard;
import com.platfos.pongift.giftcard.model.SMSTemplate;
import com.platfos.pongift.giftcard.service.GiftCardService;
import com.platfos.pongift.goods.model.Goods;
import com.platfos.pongift.goods.model.GoodsChannel;
import com.platfos.pongift.goods.service.GoodsService;
import com.platfos.pongift.quantum.service.QuantumService;
import com.platfos.pongift.security.AES256Util;
import com.platfos.pongift.security.SHA256Util;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.trade.exception.TradeException;
import com.platfos.pongift.trade.model.Trade;
import com.platfos.pongift.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TradeService {
    private final SIMCodeService codeService;
    private final MIMServiceService mimService;
    private final POMTradeService tradeService;
    private final POMTradeGiftService tradeGiftService;
    private final CIMLedgerService ledgerService;
    private final POMCustomerInfoService customerInfoService;
    private final POMDispatchInfoService dispatchInfoService;
    private final GiftCardService giftCardService;
    private final SIMTemplateInfoService templateInfoService;
    private final GoodsService goodsService;
    private final GIMChannelInfoService channelInfoService;
    private final QuantumService quantumService;

    /**
     * 상품권 정책 관리/정보 서비스
     **/
    private final SIMPolicyInfoService policyInfoService;

    /**
     * 시스템 공통 정보 서비스(DB)
     **/
    private final SIMSystemInfoService systemInfoService;

    /**
     * 유통채널 Gateway 서비스
     **/
    private final GWService gwService;

    public POMTrade getPOMTrade(String tradeId) {
        return tradeService.getPOMTrade(tradeId);
    }

    public POMTradeGift getPOMTradeGiftByLedgerIdAndTradeTp(String ledgerId, TradeTp tradeTp) {
        return tradeGiftService.getPOMTradeGiftByLedgerIdAndTradeTp(ledgerId, tradeTp);
    }

    public List<GWTradeApprovedResponse> tradeSales(Trade trade) throws Exception {
        FormTp formTp = trade.getTradeTp() == TradeTp.SEND_DIRECT ? FormTp.SEND_DIRECT : FormTp.SALES;
        return tradeSales(trade, formTp);
    }

    /**
     * 판매거래내역 생성 / 상품권 발송
     *
     * @param trade  createOrderTrade로 생성된 주문 데이터
     * @param formTp 발송템플릿
     * @throws Exception
     */
    public List<GWTradeApprovedResponse> tradeSales(Trade trade, FormTp formTp) throws Exception {
        SIMPolicyInfo policyInfo = policyInfoService.getSIMPolicyInfo();
        String customerCenterTel = systemInfoService.getValue("customer.center.tel");
        String giftStampDutyInfo = systemInfoService.getValue("gift.stamp.duty.info");
        SIMCode expiryGbCode = codeService.getSIMCode(SIMCodeGroup.expiryGb, trade.getExpiryGb());

        SendGb sendGb = trade.getTradeTp() == TradeTp.SEND_DIRECT ? SendGb.SEND_DIRECT : SendGb.SALES;
        ModifyTp modifyTp = trade.getTradeTp() == TradeTp.SEND_DIRECT ? ModifyTp.SEND_DIRECT : ModifyTp.SALES;

        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(trade.getGoodsId(), "07", null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        //수기발송 재고 체크
        if(formTp.getCode().equals(TradeTp.SALES.getCode())){
            if (channelInfo.getStockCnt() < trade.getOrderCnt()) {
                goodsService.updateGoods(goods, true);
                updateGIMGoodsStock("07", trade.getGoodsId(), 0); // 폰기프트 상품 재고 수정
                throw new TradeException(ResponseCode.FAIL_OUT_OF_STOCK, "out of stock"); // todo: 메시지 정의
            } else {
                updateGIMGoodsStock("07", trade.getGoodsId(), -trade.getOrderCnt()); // - (minus) 부호 주의
            }
        }

        List<GWTradeApprovedResponse> tradeApprovedResponseList = new ArrayList<>();
        for (int i = 0; i < trade.getOrderCnt(); i++) {
            String pinNumber = quantumService.pinNumber();
            String aes256EncodedPinNumber = AES256Util.encode(pinNumber);

            CIMLedger cimLedger = CIMLedger.builder()
                    .serviceId(trade.getSupplyServiceId())
                    .giftNo(SHA256Util.encode(pinNumber))
                    .giftNoAes256(aes256EncodedPinNumber) // 2023-05-24 상품권 번호 암호화 방식 변경
                    .giftAmt(trade.getOrderPrice())
                    .bareNo(pinNumber.substring(pinNumber.length() - 4))
                    .expiryDay(ExpiryDay.findByCode(expiryGbCode.getCodeCd()))
                    .giftSt(GiftSt.UNUSED.getCode())
                    .build();

            String ledgerId = ledgerService.insertCIMLedger(cimLedger);
            cimLedger.setLedgerId(ledgerId);

            CIMLedger ledger = ledgerService.getCIMLedger(cimLedger.getLedgerId());

            CIMLedgerHis ledgerHis = CIMLedgerHis.builder()
                    .ledgerId(ledger.getLedgerId())
                    .modifyTp(modifyTp.getCode())
                    .expiryDt(ledger.getExpiryDt())
                    .giftSt(ledger.getGiftSt())
                    .build();
            ledgerService.insertCIMLedgerHis(ledgerHis);

            POMTradeGift pomTradeGift = POMTradeGift.builder()
                    .tradeId(trade.getTradeId())
                    .ledgerId(ledger.getLedgerId())
                    .goodsTp(trade.getGiftCardTp().getCode())
                    .goodsNm(trade.getGoodsNm())
                    .goodsPrice(trade.getOrderPrice())
                    .discountPrice(trade.getRetailPrice() - trade.getOrderPrice())
                    .build();
            String productId = tradeGiftService.insertPOMTradeGift(pomTradeGift);
            pomTradeGift.setProductId(productId);

            POMCustomerInfo pomCustomerInfo = POMCustomerInfo.builder()
                    .productId(pomTradeGift.getProductId())
                    .buyerNm(trade.getBuyer().getName())
                    .buyerPhone(AES256Util.encode(trade.getBuyer().getPhoneNo()))
                    .buyerId(trade.getBuyer().getBuyerId())
                    .receiverNm(trade.getReceiver().getName())
                    .receiverPhone(AES256Util.encode(trade.getReceiver().getPhoneNo()))
                    .build();
            String customerId = customerInfoService.insertPOMCustomerInfo(pomCustomerInfo);
            pomCustomerInfo.setCustomerId(customerId);

            POMDispatchInfo dispatchInfo = POMDispatchInfo.builder()
                    .productId(pomTradeGift.getProductId())
                    .serviceId(trade.getSupplyServiceId())
                    .customerId(pomCustomerInfo.getCustomerId())
                    .receiverPhone(AES256Util.encode(trade.getReceiver().getPhoneNo()))
                    .sendGb(sendGb.getCode())
                    .sendCnt(0)
                    .build();
            String dispatchId = dispatchInfoService.insertPOMDispatchInfo(dispatchInfo);
            dispatchInfo.setDispatchId(dispatchId);

            String publicCode = AES256Util.encodeHex(ledger.getLedgerId());

            Map<String, String> smsTemplateMap = new HashMap<>();
            smsTemplateMap.put("contents", trade.getGoodsNm());
            smsTemplateMap.put("serviceNm", trade.getServiceNm());
            smsTemplateMap.put("centerTel", trade.getCenterTel());
            smsTemplateMap.put("goodsImage", trade.getGoodsImage());
            smsTemplateMap.put("goodsNm", trade.getGoodsNm());
            smsTemplateMap.put("giftExpiryDt", Util.timestamp2strdate(Util.strdate2timestamp(ledger.getExpiryDt(), "yyyyMMdd"), "yy.MM.dd"));
            smsTemplateMap.put("purchaseDt", Util.timestamp2strdate(Util.strdate2timestamp(trade.getOrderDate(), "yyyyMMddHHmmss"), "yy.MM.dd"));
            smsTemplateMap.put("purchaseCnt", "1");
            smsTemplateMap.put("purchaseAmount", String.valueOf(trade.getOrderPrice()));
            smsTemplateMap.put("goodsExchangePlace", trade.getExchangePlace());
            smsTemplateMap.put("guide", "-");
//            smsTemplateMap.put("giftBarcode", pinNumber);


            smsTemplateMap.put("buyerPhoneNo", Util.strToPhoneNumber(trade.getBuyer().getPhoneNo()));
            smsTemplateMap.put("receiverPhoneNo", Util.strToPhoneNumber(trade.getReceiver().getPhoneNo()));
            smsTemplateMap.put("publicLegerId", ledger.getLedgerId().replace("GL", ""));
            smsTemplateMap.put("goodsInfo", trade.getGoodsInfo());
            smsTemplateMap.put("expireDt", policyInfo.getExpireDt());
            smsTemplateMap.put("customerCenterTel", Util.strToPhoneNumber(customerCenterTel));
            smsTemplateMap.put("giftStampDutyInfo", giftStampDutyInfo);
            smsTemplateMap.put("publicCode", publicCode);
            smsTemplateMap.put("giftAmtText", new DecimalFormat(",###.##").format(trade.getRetailPrice()) + "원"); // 2023-01-09 소비자가로 변경 요청
            smsTemplateMap.put("giftAmt", String.valueOf(trade.getRetailPrice()));

            SIMTemplateInfo templateInfo = templateInfoService.getSIMTemplateInfoByFormTp(formTp);

            SMSTemplate smsTemplate = SMSTemplate.builder()
                    .templateId(templateInfo.getTemplateId())
                    .phoneNo(trade.getReceiver().getPhoneNo())
                    .dispatchId(dispatchInfo.getDispatchId())
                    .smsTemplateMap(smsTemplateMap)
                    .build();

            giftCardService.asyncSms(smsTemplate);


            GWTradeApprovedResponse gwTradeApprovedResponse = GWTradeApprovedResponse.builder()
                    .giftNo(aes256EncodedPinNumber)
                    .detailNo(trade.getDetailNo())
                    .expiryDate(ledger.getExpiryDt())
                    .publicCode(publicCode)
                    .build();
            tradeApprovedResponseList.add(gwTradeApprovedResponse);
        }

        return tradeApprovedResponseList;
    }

    @Transactional
    public void tradeSalesCancel(Trade trade, String channelGb) throws Exception {
        List<CIMLedger> ledgers = ledgerService.selectCIMLedgerList(TradeTp.SALES.getCode(), trade.getOrderNo(), trade.getDetailNo());

        for (CIMLedger ledger : ledgers) {
            if (GiftSt.findByCode(ledger.getGiftSt()) == GiftSt.UNUSED) {
                ledgerService.updateCIMLedger(ledger.getLedgerId(), null, null, null, GiftSt.STOP.getCode());

                CIMLedgerHis ledgerHis = CIMLedgerHis.builder()
                        .ledgerId(ledger.getLedgerId())
                        .modifyTp(ModifyTp.SALES_CANCEL.getCode())
                        .expiryDt(ledger.getExpiryDt())
                        .giftSt(GiftSt.STOP.getCode())
                        .build();
                ledgerService.insertCIMLedgerHis(ledgerHis);

                POMTradeGift pomTradeGift = POMTradeGift.builder()
                        .tradeId(trade.getTradeId())
                        .ledgerId(ledger.getLedgerId())
                        .goodsTp(trade.getGiftCardTp().getCode())
                        .goodsNm(trade.getGoodsNm())
                        .goodsPrice(trade.getOrderPrice())
                        .build();
                String productId = tradeGiftService.insertPOMTradeGift(pomTradeGift);
                pomTradeGift.setProductId(productId);
            }
        }

        updateGIMGoodsStock(channelGb, trade.getGoodsId(), trade.getOrderCnt());
    }

    /**
     * 반품 요청 거래내역 생성
     *
     * @param trade
     * @throws Exception
     */
    public void tradeReturnRequest(Trade trade) throws Exception {
        List<CIMLedger> ledgers = ledgerService.selectCIMLedgerList(TradeTp.SALES.getCode(), trade.getOrderNo(), trade.getDetailNo());

        for (CIMLedger ledger : ledgers) {
            POMTradeGift pomTradeGift = POMTradeGift.builder()
                    .tradeId(trade.getTradeId())
                    .ledgerId(ledger.getLedgerId())
                    .goodsTp(trade.getGiftCardTp().getCode())
                    .goodsNm(trade.getGoodsNm())
                    .goodsPrice(trade.getOrderPrice())
                    .build();
            tradeGiftService.insertPOMTradeGift(pomTradeGift);

            POMCustomerInfo pomCustomerInfo = POMCustomerInfo.builder()
                    .productId(pomTradeGift.getProductId())
                    .buyerNm(trade.getBuyer().getName())
                    .buyerPhone(AES256Util.encode(trade.getBuyer().getPhoneNo()))
                    .buyerId(trade.getBuyer().getBuyerId())
                    .receiverNm(trade.getReceiver().getName())
                    .receiverPhone(AES256Util.encode(trade.getReceiver().getPhoneNo()))
                    .build();
            customerInfoService.insertPOMCustomerInfo(pomCustomerInfo);
        }
    }

    /**
     * 반품 요청 중복 거래 여부 확인
     *
     * @param request
     * @param tradeTp
     * @return boolean
     */
    public boolean isDuplicateReturnTrade(GWTradeApprovedRequest request, TradeTp tradeTp) {
        MIMService channelService = mimService.getMIMServiceByChannelGb(request.getChannelGb());
        POMTrade pomTrade = new POMTrade();
        pomTrade.setServiceId(channelService.getServiceId());
        pomTrade.setTradeTps(new String[]{tradeTp.getCode()});
        pomTrade.setOrderNo(request.getTradeId());
        pomTrade.setDetailNo(request.getGoods().getProductOrderId());
        int requestCount = tradeService.countPOMTrade(pomTrade);
        if (requestCount >= 1) {
            pomTrade.setTradeTps(new String[]{TradeTp.RETURN_CANCEL.getCode(), TradeTp.RETURN_REJECT.getCode()});
            int cancelCount = tradeService.countPOMTrade(pomTrade);
            // 반품 요청, 취소 거래 건수가 같으면 중복 거래 X
            return requestCount != cancelCount;
        } else {
            return false;
        }
    }

    public POMTradeCreateResult insertTrade(Trade trade) throws JsonProcessingException {
        String orderDt;
        String orderTm;
        try {
            orderDt = Util.timestamp2strdate(Util.strdate2timestamp(trade.getOrderDate(), "yyyyMMddHHmmss"), "yyyyMMdd");
            orderTm = Util.timestamp2strdate(Util.strdate2timestamp(trade.getOrderDate(), "yyyyMMddHHmmss"), "HHmmss");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        POMTrade pomTrade = POMTrade.builder()
                .serviceId(trade.getServiceId())
                .storeId(trade.getStoreId())
                .goodsId(trade.getGoodsId())
                .tradeTp(trade.getTradeTp().getCode())
                .orderNo(trade.getOrderNo())
                .detailNo(trade.getDetailNo())
                .orderAmt(trade.getOrderPrice() * trade.getOrderCnt())
                .orderDt(orderDt)
                .orderTm(orderTm)
                .orderCnt(trade.getOrderCnt())
                .paymentGb(trade.getBuyer().getPaymentGb())
                .build();

        return tradeService.createTrade(pomTrade);
    }

    public void checkInsertTradeResult(POMTradeCreateResult result) throws TradeException {
        if (!result.getResultCode().equals("0000")) {
            if (result.getResultCode().equals("0001")) {
                throw new TradeException(ResponseCode.FAIL_TRADE_REQUEST_DUPLICATE, result.getResultMessage());
            } else if (result.getResultCode().equals("0002")) {
                throw new TradeException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, result.getResultMessage());
            } else if (result.getResultCode().equals("0003")) {
                throw new TradeException(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, result.getResultMessage());
            } else {
                throw new TradeException(ResponseCode.FAIL, result.getResultMessage());
            }
        }
    }

    public void checkStockCnt(String channelGb, Trade trade, Goods goods, GIMChannelInfo channelInfo) throws Exception {
        // 재고가 없는 경우
        //제휴몰 분기
        if(channelInfo.getChannelGb().equals("08")){
            if (channelInfo.getStockCnt() < trade.getOrderCnt()) {
                updateGIMGoodsMarketStock(channelGb, trade.getGoodsId()); // 채널 상품 품절 처리
                updateGIMGoodsStockAndSoldOut(trade.getGoodsId()); // 폰기프트 상품 재고 수정
                setChannelSoldOut(channelGb, trade.getChannelId()); //제휴몰 상품 품절 처리
            } else {
                updateGIMGoodsStock(channelGb, trade.getGoodsId(), -trade.getOrderCnt()); // - (minus) 부호 주의
            }
        }else{
            if (channelInfo.getStockCnt() < trade.getOrderCnt()) {
                goodsService.updateGoods(goods, true);
                setChannelCancelSale(channelGb, trade, CancelSaleReasonCd.SOLD_OUT); // 품절 상품 판매(구매) 취소 처리
                updateGIMGoodsStock(channelGb, trade.getGoodsId(), 0); // 폰기프트 상품 재고 수정
                setChannelSoldOut(channelGb, trade.getChannelId()); // 채널 상품 품절 처리
                throw new TradeException(ResponseCode.FAIL_OUT_OF_STOCK, "out of stock"); // todo: 메시지 정의
            } else {
                updateGIMGoodsStock(channelGb, trade.getGoodsId(), -trade.getOrderCnt()); // - (minus) 부호 주의
            }
        }

    }

    /**
     * 거래유형 별 금액 부호
     *
     * @param tradeTp
     * @return sign
     */
    private int getSignByTradeTp(TradeTp tradeTp) {
        int sign;
        switch (tradeTp) {
            case SALES_CANCEL:
            case RETURN:
            case RETURN_REQUESTED:
                sign = -1;
                break;
            default:
                sign = 1;
        }
        return sign;
    }

    public Trade createSaleTrade(GWTradeApprovedRequest approvedRequest, String channelServiceId, Goods goods, GIMChannelInfo channelInfo, TradeTp tradeTp) throws Exception {
        Trade trade = createOrderTrade(approvedRequest, channelServiceId, goods, channelInfo, TradeTp.SALES);
        checkStockCnt(approvedRequest.getChannelGb(), trade, goods, channelInfo);
        return trade;
    }

    public List<Trade> createMultiTradeList(GWMultiTradeApprovedRequest multiTradeApprovedRequest, String channelServiceId, Goods goods, GIMChannelInfo channelInfo, TradeTp tradeTp) throws Exception {
        List<Trade> resultTradeList = new ArrayList<>();
        for (GWMultiTrade multiTrade : multiTradeApprovedRequest.getMultiTradeList()) {
            // 상품공급사 서비스 조회
            MIMService supplyService = mimService.getMIMService(goods.getServiceId());

            // 소비자가 정리
            int retailPrice = (multiTradeApprovedRequest.getGoods().getRetailPrice() == null) ? 0 : multiTradeApprovedRequest.getGoods().getRetailPrice();
            if (retailPrice <= 0) retailPrice = goods.getRetailPrice();

            Trade trade = Trade.builder()
                    .serviceId(channelServiceId)
                    .supplyServiceId(goods.getServiceId())
                    .goodsId(goods.getGoodsId())
                    .serviceNm(supplyService.getServiceNm())
                    .centerTel(supplyService.getCenterTel())
                    .goodsNm(multiTradeApprovedRequest.getGoods().getGoodsNm())
                    .goodsImage(goods.getGoodsImage())
                    .goodsInfo(goods.getGoodsInfo())
                    .exchangePlace(goods.getExchangePlace())
                    .orderNo(multiTradeApprovedRequest.getTradeId())
                    .detailNo(multiTrade.getProductOrderId())
                    .orderPrice(Math.abs(multiTradeApprovedRequest.getGoods().getSalePrice()) * getSignByTradeTp(tradeTp))
                    .orderDate(multiTradeApprovedRequest.getTradeDate())
                    .orderCnt(1)
                    .buyer(multiTradeApprovedRequest.getBuyer())
                    .receiver(multiTrade.getReceiver())
                    .giftCardTp(GiftCardTp.findByCode(goods.getGiftcardTp()))
                    .tradeTp(tradeTp)
                    .expiryGb(goods.getExpiryGb())
                    .retailPrice(retailPrice)
                    .channelId(channelInfo.getChannelId())
                    .build();

            // pom_trade_list_reg01_sp 프로시져 호출 (거래내역 생성)
            POMTradeCreateResult result = insertTrade(trade);
            checkInsertTradeResult(result);
            trade.setTradeId(result.getPomTrade().getTradeId());
            checkStockCnt(multiTradeApprovedRequest.getChannelGb(), trade, goods, channelInfo);

            resultTradeList.add(trade);
        }

        return resultTradeList;
    }

    /**
     * 주문 거래내역 생성 (pom_trade_list_reg01_sp 프로시져 호출)
     *
     * @param approvedRequest 승인 요청 request
     * @param tradeTp         주문 유형
     * @throws Exception
     */
    public Trade createOrderTrade(GWTradeApprovedRequest approvedRequest, String channelServiceId, Goods goods, GIMChannelInfo channelInfo, TradeTp tradeTp) throws Exception {
        // 상품공급사 서비스 조회
        MIMService supplyService = mimService.getMIMService(goods.getServiceId());

        // 소비자가 정리
        int retailPrice = (approvedRequest.getGoods().getRetailPrice() == null) ? 0 : approvedRequest.getGoods().getRetailPrice();
        if (retailPrice <= 0) retailPrice = goods.getRetailPrice();

        Trade trade = Trade.builder()
                .serviceId(channelServiceId)
                .supplyServiceId(goods.getServiceId())
                .goodsId(goods.getGoodsId())
                .serviceNm(supplyService.getServiceNm())
                .centerTel(supplyService.getCenterTel())
                .goodsNm(approvedRequest.getGoods().getGoodsNm())
                .goodsImage(goods.getGoodsImage())
                .goodsInfo(goods.getGoodsInfo())
                .exchangePlace(goods.getExchangePlace())
                .orderNo(approvedRequest.getTradeId())
                .detailNo(approvedRequest.getGoods().getProductOrderId())
                .orderPrice(Math.abs(approvedRequest.getGoods().getSalePrice()) * getSignByTradeTp(tradeTp))
                .orderDate(approvedRequest.getTradeDate())
                .orderCnt(approvedRequest.getGoods().getSaleCnt())
                .buyer(approvedRequest.getBuyer())
                .receiver(approvedRequest.getReceiver())
                .giftCardTp(GiftCardTp.findByCode(goods.getGiftcardTp()))
                .tradeTp(tradeTp)
                .expiryGb(goods.getExpiryGb())
                .retailPrice(retailPrice)
                .channelId(channelInfo.getChannelId())
                .build();

        // pom_trade_list_reg01_sp 프로시져 호출 (거래내역 생성)
        POMTradeCreateResult result = insertTrade(trade);
        checkInsertTradeResult(result);

        trade.setTradeId(result.getPomTrade().getTradeId());
        return trade;
    }

    public List<GWGiftResponse> tradeGift(GWTradeApprovedRequest request) throws Exception {
        List<GWGiftResponse> giftResponses = new ArrayList<>();

        MIMService service = mimService.getMIMServiceByChannelGb(request.getChannelGb());
        GIMChannelInfo channelInfo = channelInfoService.selectGIMChannelInfoByChannelIdAndChannelGb(request.getGoods().getGoodsId(), request.getChannelGb(), null);
        Goods goods = goodsService.getGoods(channelInfo.getGoodsId());

        MIMService supplyService = mimService.getMIMService(goods.getServiceId());

        Integer retailPrice = (request.getGoods().getRetailPrice() == null) ? 0 : request.getGoods().getRetailPrice();
        if (retailPrice <= 0) retailPrice = goods.getRetailPrice();

        Trade trade = Trade.builder()
                .serviceId(service.getServiceId())
                .supplyServiceId(goods.getServiceId())
                .goodsId(goods.getGoodsId())
                .serviceNm(supplyService.getServiceNm())
                .goodsNm(request.getGoods().getGoodsNm())
                .goodsImage(goods.getGoodsImage())
                .exchangePlace(goods.getExchangePlace())
                .goodsInfo(goods.getGoodsInfo())
                .orderNo(request.getTradeId())
                .detailNo(request.getGoods().getProductOrderId())
                .orderPrice(Math.abs(request.getGoods().getSalePrice()))
                .orderDate(request.getTradeDate())
                .orderCnt(request.getGoods().getSaleCnt())
                .buyer(request.getBuyer())
                .receiver(request.getReceiver())
                .giftCardTp(GiftCardTp.findByCode(goods.getGiftcardTp()))
                .tradeTp(TradeTp.GIFT)
                .expiryGb(goods.getExpiryGb())
                .retailPrice(retailPrice)
                .build();

        POMTradeCreateResult result = insertTrade(trade);
        if (result.getResultCode().equals("0000")) {
            trade.setTradeId(result.getPomTrade().getTradeId());

            SIMCode expiryGbCode = codeService.getSIMCode(SIMCodeGroup.expiryGb, trade.getExpiryGb());
            for (int i = 0; i < trade.getOrderCnt(); i++) {
                String pinNumber = quantumService.pinNumber();
                String aes256EncodedPinNumber = AES256Util.encode(pinNumber);

                CIMLedger cimLedger = CIMLedger.builder()
                        .serviceId(trade.getSupplyServiceId())
                        .giftNo(SHA256Util.encode(pinNumber))
                        .giftNoAes256(aes256EncodedPinNumber) // 2023-05-24 상품권 번호 암호화 방식 변경
                        .giftAmt(trade.getRetailPrice())
                        .bareNo(pinNumber.substring(pinNumber.length() - 4))
                        .expiryDay(ExpiryDay.findByCode(expiryGbCode.getCodeCd()))
                        .giftSt(GiftSt.UNUSED.getCode())
                        .build();

                String ledgerId = ledgerService.insertCIMLedger(cimLedger);
                cimLedger.setLedgerId(ledgerId);

                CIMLedger ledger = ledgerService.getCIMLedger(cimLedger.getLedgerId());

                CIMLedgerHis ledgerHis = CIMLedgerHis.builder()
                        .ledgerId(ledger.getLedgerId())
                        .modifyTp(ModifyTp.GIFT.getCode())
                        .expiryDt(ledger.getExpiryDt())
                        .giftSt(ledger.getGiftSt())
                        .build();
                ledgerService.insertCIMLedgerHis(ledgerHis);

                POMTradeGift pomTradeGift = POMTradeGift.builder()
                        .tradeId(trade.getTradeId())
                        .ledgerId(ledger.getLedgerId())
                        .goodsTp(trade.getGiftCardTp().getCode())
                        .goodsNm(trade.getGoodsNm())
                        .goodsPrice(trade.getOrderPrice())
                        .discountPrice(trade.getRetailPrice() - trade.getOrderPrice())
                        .build();
                String productId = tradeGiftService.insertPOMTradeGift(pomTradeGift);
                pomTradeGift.setProductId(productId);

                POMCustomerInfo pomCustomerInfo = POMCustomerInfo.builder()
                        .productId(pomTradeGift.getProductId())
                        .buyerNm(trade.getBuyer().getName())
                        .buyerPhone(AES256Util.encode(trade.getBuyer().getPhoneNo()))
                        .buyerId(trade.getBuyer().getBuyerId())
                        .receiverNm(trade.getReceiver().getName())
                        .receiverPhone(AES256Util.encode(trade.getReceiver().getPhoneNo()))
                        .build();
                String customerId = customerInfoService.insertPOMCustomerInfo(pomCustomerInfo);
                pomCustomerInfo.setCustomerId(customerId);

                GWGiftResponse giftResponse = GWGiftResponse.builder()
                        .barcodeNo(pinNumber)
                        .expiryDate(ledger.getExpiryDt())
                        .productOrderId(request.getGoods().getProductOrderId())
                        .build();
                giftResponses.add(giftResponse);
            }
        } else if (result.getResultCode().equals("0001")) {
            throw new TradeException(ResponseCode.FAIL_TRADE_REQUEST_DUPLICATE);
        } else if (result.getResultCode().equals("0002")) {
            throw new TradeException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, result.getResultMessage());
        } else if (result.getResultCode().equals("0003")) {
            throw new TradeException(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, result.getResultMessage());
        } else {
            throw new TradeException(ResponseCode.FAIL, result.getResultMessage());
        }

        return giftResponses;
    }

    @Transactional
    public GiftCardTradeResponse tradeUse(Store store, CIMLedger ledger, GiftCardTrade giftCardTrade, Goods goods, POMTradeGift tradeGift, TradeTp tradeTp) throws Exception {
        POMTrade pomTrade = POMTrade.builder()
                .serviceId(store.getServiceId())
                .storeId(store.getStoreId())
                .goodsId(goods.getGoodsId())
                .ledgerId(ledger.getLedgerId())
                .tradeTp(tradeTp.getCode())
                .orderNo(giftCardTrade.getOrderNo())
                .detailNo(giftCardTrade.getOrderNo())
                .orderAmt(Math.abs(ledger.getGiftAmt()) * (tradeTp == TradeTp.USE_CANCEL ? -1 : 1))
                .orderDt(giftCardTrade.getOrderDt())
                .orderTm(giftCardTrade.getOrderTm())
                .orderCnt(1)
                .build();

        POMTradeCreateResult result = tradeService.createTrade(pomTrade);
        if (result.getResultCode().equals("0000")) {
            String tradeId = result.getPomTrade().getTradeId();
            GiftSt giftSt = null;
            ModifyTp modifyTp = null;
            if (tradeTp == TradeTp.USE) {
                giftSt = GiftSt.USED;
                modifyTp = ModifyTp.USE;
            } else if (tradeTp == TradeTp.USE_CANCEL) {
                giftSt = GiftSt.UNUSED;
                modifyTp = ModifyTp.USE_CANCEL;
            }

            ledgerService.updateCIMLedger(ledger.getLedgerId(), null, null, null, giftSt.getCode());

            CIMLedgerHis ledgerHis = CIMLedgerHis.builder()
                    .ledgerId(ledger.getLedgerId())
                    .modifyTp(modifyTp.getCode())
                    .expiryDt(ledger.getExpiryDt())
                    .giftSt(giftSt.getCode())
                    .build();
            ledgerService.insertCIMLedgerHis(ledgerHis);

            POMTradeGift pomTradeGift = POMTradeGift.builder()
                    .tradeId(tradeId)
                    .ledgerId(ledger.getLedgerId())
                    .goodsTp(tradeGift.getGoodsTp())
                    .goodsNm(tradeGift.getGoodsNm())
                    .goodsPrice(pomTrade.getOrderAmt())
                    .build();
            tradeGiftService.insertPOMTradeGift(pomTradeGift);
            POMTrade pomTradeResult = tradeService.getPOMTrade(result.getPomTrade().getTradeId());
            return GiftCardTradeResponse.builder()
                    .storeId(goods.getStoreId())
                    .goodsId(goods.getGoodsId())
                    .orderNo(giftCardTrade.getOrderNo())
                    .tradePrice(ledger.getGiftAmt())
                    .approvalNo(result.getPomTrade().getApprovalNo())
                    .approvalDt(pomTradeResult.getApprovalDt())
                    .approvalTm(pomTradeResult.getApprovalTm())
                    .build();
        } else {
            switch (result.getResultCode()) {
                case "0001":
                    throw new TradeException(ResponseCode.FAIL_TRADE_REQUEST_DUPLICATE);
                case "0002":
                    throw new TradeException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, result.getResultMessage());
                case "0003":
                    throw new TradeException(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, result.getResultMessage());
                default:
                    throw new TradeException(ResponseCode.FAIL, result.getResultMessage());
            }
        }
    }

    public void tradeResend(CIMLedger ledger, ModifyTp modifyTp, ResendGiftCard resendGiftCard) throws Exception {
        tradeResend(ledger, null, modifyTp, resendGiftCard);
    }

    public void tradeResend(CIMLedger ledger, String phoneNumber, ModifyTp modifyTp, ResendGiftCard resendGiftCard) throws
            Exception {
        int giftMaxResendCnt = 3;
        SendGb sendGb = modifyTp == ModifyTp.RESEND ? SendGb.RESEND : SendGb.CHANGE_NUMBER;
        FormTp formTp = modifyTp == ModifyTp.RESEND ? FormTp.RESEND : FormTp.CHANGE_NUMBER;
        TradeTp tradeTp = resendGiftCard.getTradeTp().equals(TradeTp.SEND_DIRECT.getCode()) ? TradeTp.SEND_DIRECT : TradeTp.SALES;

        if (resendGiftCard.getTradeTp().equals(TradeTp.SEND_DIRECT.getCode())) {
            formTp = FormTp.SEND_DIRECT;
        }

        POMTradeGift tradeGift = tradeGiftService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), tradeTp);

        int resendCount = (dispatchInfoService.getResendCountByProductId(tradeGift.getProductId())) + 1;
        if (resendCount > giftMaxResendCnt) {
            throw new GiftException(ResponseCode.FAIL_GIFT_RESEND_COUNT_EXCEEDED, String.valueOf(giftMaxResendCnt));
        }

        POMCustomerInfo customerInfo = customerInfoService.getPOMCustomerInfoByProductId(tradeGift.getProductId());
        Goods goods = goodsService.getGoodsORSendGoodsByLedgerId(ledger.getLedgerId());
        POMTrade trade = tradeService.getPOMTrade(tradeGift.getTradeId());

        String pinNumber = quantumService.pinNumber();

        ledgerService.updateCIMLedger(ledger.getLedgerId(), SHA256Util.encode(pinNumber), pinNumber.substring(pinNumber.length() - 4), null, null);

        CIMLedgerHis ledgerHis = CIMLedgerHis.builder()
                .ledgerId(ledger.getLedgerId())
                .modifyTp(modifyTp.getCode())
                .expiryDt(ledger.getExpiryDt())
                .giftSt(ledger.getGiftSt())
                .build();
        ledgerService.insertCIMLedgerHis(ledgerHis);

        if (modifyTp == ModifyTp.RESEND) {
            phoneNumber = AES256Util.decode(customerInfo.getReceiverPhone());
        } else {
            customerInfoService.updatePOMCustomerInfo(customerInfo.getCustomerId(), null, null, null, AES256Util.encode(phoneNumber));
        }

        POMDispatchInfo dispatchInfo = POMDispatchInfo.builder()
                .productId(tradeGift.getProductId())
                .serviceId(goods.getServiceId())
                .customerId(customerInfo.getCustomerId())
                .receiverPhone(AES256Util.encode(phoneNumber))
                .sendGb(sendGb.getCode())
                .sendCnt(0)
                .build();
        String dispatchId = dispatchInfoService.insertPOMDispatchInfo(dispatchInfo);
        dispatchInfo.setDispatchId(dispatchId);

        MIMService supplyService = mimService.getMIMService(goods.getServiceId());


        Map<String, String> smsTemplateMap = new HashMap<>();
        smsTemplateMap.put("giftResendCnt", String.valueOf(resendCount));
        smsTemplateMap.put("giftMaxResendCnt", String.valueOf(giftMaxResendCnt));
        smsTemplateMap.put("serviceNm", supplyService.getServiceNm());
        smsTemplateMap.put("centerTel", supplyService.getCenterTel());
        smsTemplateMap.put("contents", tradeGift.getGoodsNm());
        smsTemplateMap.put("goodsImage", goods.getGoodsImage());
        smsTemplateMap.put("goodsNm", tradeGift.getGoodsNm());
        smsTemplateMap.put("giftExpiryDt", Util.timestamp2strdate(Util.strdate2timestamp(ledger.getExpiryDt(), "yyyyMMdd"), "yy.MM.dd"));
        smsTemplateMap.put("purchaseDt", Util.timestamp2strdate(Util.strdate2timestamp(trade.getOrderDt() + trade.getOrderTm(), "yyyyMMddHHmmss"), "yy.MM.dd"));
        smsTemplateMap.put("purchaseCnt", "1");
        smsTemplateMap.put("purchaseAmount", String.valueOf(tradeGift.getGoodsPrice())); // 2022-04-20 실판매가로 변경 요청
        smsTemplateMap.put("goodsExchangePlace", goods.getExchangePlace());
        smsTemplateMap.put("guide", "-");
//        smsTemplateMap.put("giftBarcode", pinNumber);

        SIMPolicyInfo policyInfo = policyInfoService.getSIMPolicyInfo();
        String customerCenterTel = systemInfoService.getValue("customer.center.tel");
        String giftStampDutyInfo = systemInfoService.getValue("gift.stamp.duty.info");

        smsTemplateMap.put("buyerPhoneNo", Util.strToPhoneNumber(AES256Util.decode(customerInfo.getBuyerPhone())));
        smsTemplateMap.put("receiverPhoneNo", Util.strToPhoneNumber(phoneNumber));
        smsTemplateMap.put("publicLegerId", ledger.getLedgerId().replace("GL", ""));
        smsTemplateMap.put("goodsInfo", goods.getGoodsInfo());
        smsTemplateMap.put("expireDt", policyInfo.getExpireDt());
        smsTemplateMap.put("customerCenterTel", Util.strToPhoneNumber(customerCenterTel));
        smsTemplateMap.put("giftStampDutyInfo", giftStampDutyInfo);
        smsTemplateMap.put("publicCode", AES256Util.encodeHex(ledger.getLedgerId()));
        smsTemplateMap.put("giftAmtText", new DecimalFormat(",###.##").format(tradeGift.getGoodsPrice()) + "원");
        smsTemplateMap.put("giftAmt", String.valueOf(ledger.getGiftAmt()));

        SIMTemplateInfo templateInfo = templateInfoService.getSIMTemplateInfoByFormTp(formTp);
        SMSTemplate smsTemplate = SMSTemplate.builder()
                .templateId(templateInfo.getTemplateId())
                .phoneNo(phoneNumber)
                .dispatchId(dispatchInfo.getDispatchId())
                .smsTemplateMap(smsTemplateMap)
                .build();

        giftCardService.asyncSms(smsTemplate);
    }

    @Async
    public void asyncTradeExpiredNotice() throws Exception {
        List<CIMLedger> ledgers = ledgerService.selectExpiredNoticeCIMLedgerList();
        for (CIMLedger ledger : ledgers) {
            tradeExpiredNotice(ledger);
        }
    }

    public void tradeExpiredNotice(CIMLedger ledger) throws Exception {
        POMTradeGift tradeGift = tradeGiftService.getPOMTradeGiftByLedgerIdAndTradeTp(ledger.getLedgerId(), TradeTp.SALES);

        if (dispatchInfoService.getExpiredNoticeByProductId(tradeGift.getProductId()) == 0) {
            POMCustomerInfo customerInfo = customerInfoService.getPOMCustomerInfoByProductId(tradeGift.getProductId());
            Goods goods = goodsService.getGoodsByLedgerId(ledger.getLedgerId());
            POMTrade trade = tradeService.getPOMTrade(tradeGift.getTradeId());

            POMDispatchInfo dispatchInfo = POMDispatchInfo.builder()
                    .productId(tradeGift.getProductId())
                    .serviceId(goods.getServiceId())
                    .customerId(customerInfo.getCustomerId())
                    .receiverPhone(customerInfo.getReceiverPhone())
                    .sendGb(SendGb.EXPIRATION.getCode())
                    .sendCnt(ledger.getSendCnt())
                    .build();
            String dispatchId = dispatchInfoService.insertPOMDispatchInfo(dispatchInfo);
            dispatchInfo.setDispatchId(dispatchId);


            Map<String, String> smsTemplateMap = new HashMap<>();
            smsTemplateMap.put("remainDay", String.valueOf(ledger.getRemainDay()));
            smsTemplateMap.put("contents", "'" + tradeGift.getGoodsNm() + "' 유효기간 만료 안내");
            smsTemplateMap.put("goodsNm", tradeGift.getGoodsNm());
            smsTemplateMap.put("giftExpiryDt", Util.timestamp2strdate(Util.strdate2timestamp(ledger.getExpiryDt(), "yyyyMMdd"), "yy.MM.dd"));
            smsTemplateMap.put("purchaseDt", Util.timestamp2strdate(Util.strdate2timestamp(trade.getOrderDt() + trade.getOrderTm(), "yyyyMMddHHmmss"), "yy.MM.dd"));
            smsTemplateMap.put("tradeCnt", "1");
            smsTemplateMap.put("paymentAmount", new DecimalFormat(",###.##").format(tradeGift.getGoodsPrice()) + "원"); // 2022-04-20 실판매가로 변경 요청
            smsTemplateMap.put("goodsExchangePlace", goods.getExchangePlace());

            SIMTemplateInfo templateInfo = templateInfoService.getSIMTemplateInfoByFormTp(FormTp.EXPIRATION);
            SMSTemplate smsTemplate = SMSTemplate.builder()
                    .templateId(templateInfo.getTemplateId())
                    .phoneNo(AES256Util.decode(customerInfo.getReceiverPhone()))
                    .dispatchId(dispatchInfo.getDispatchId())
                    .smsTemplateMap(smsTemplateMap)
                    .build();

            giftCardService.sms(smsTemplate);
        }
    }

    private GoodsChannel getGIMGoodsChannel(String channelGb, String goodsId) {
        return goodsService.getGIMGoodsChannel(channelGb, goodsId);
    }

    public void setChannelSoldOut(String channelGb, String channelGoodsId) {
        GWResponseWrapper<GWGoodsResponse> channelSoldOutResponse = goodsService.setChannelSoldOut(channelGb, channelGoodsId);

        if (!channelSoldOutResponse.getResponse().isSuccess()) {
            log.warn("{}", channelSoldOutResponse.getResponse().getMessage());
        }
    }

    public void setChannelCancelSale(String channelGb, Trade trade, CancelSaleReasonCd cancelSaleReasonCd) {
        HttpEntity<?> httpEntity = gwService.createHttpEntity(
                HttpMethod.POST.name()
                , null
                , GWTradeCancel.builder()
                        .channelOrderNo(trade.getDetailNo())
                        .channelGoodsId(trade.getChannelId())
                        .orderOptionNo(0) /* todo: 위메프 주문 옵션 정보, 정책 확인 후 값 지정 */
                        .cancelSaleReasonCd(cancelSaleReasonCd)
                        .build()
        );

        GWResponseWrapper<Object> channelCancelSaleResponse = gwService.send(Object.class, channelGb, "/trade/cancelSale", HttpMethod.POST.name(), httpEntity);
        if (!channelCancelSaleResponse.getResponse().isSuccess()) {
            log.error("{}", channelCancelSaleResponse.getResponse().getMessage());
        }
    }

    public void updateGIMGoodsStock(String channelGb, String goodsId, int orderCnt) {
        goodsService.updateGIMGoodsStock(channelGb, goodsId, orderCnt);
    }

    public void updateGIMGoodsStockAndSoldOut(String goodsId) {
        goodsService.updateGIMGoodsStockAndSoldOut(goodsId);
    }

    public void updateGIMGoodsMarketStock(String channelGb, String goodsId) {
        goodsService.updateGIMGoodsMarketStock(channelGb, goodsId);
    }
}
