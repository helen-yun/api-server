package com.platfos.pongift.data.ssm;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.pom.model.POMTradeCreateResult;
import com.platfos.pongift.data.pom.service.POMTradeService;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.data.sim.service.SIMTemplateInfoService;
import com.platfos.pongift.data.ssm.dao.SSMDirectSendGiftCardDAO;
import com.platfos.pongift.data.ssm.model.SSMGiftCardHistory;
import com.platfos.pongift.data.ssm.model.SSMSendHistory;
import com.platfos.pongift.data.ssm.model.SSMSendInfo;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.gateway.model.GWBuyer;
import com.platfos.pongift.gateway.model.GWReceiver;
import com.platfos.pongift.giftcard.model.SendGoodsRequest;
import com.platfos.pongift.goods.model.Goods;
import com.platfos.pongift.security.AES256Util;
import com.platfos.pongift.trade.exception.TradeException;
import com.platfos.pongift.trade.model.Trade;
import com.platfos.pongift.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SSMDirectSendGiftCardService {
    private final SIMIndexService simIndexService;
    private final POMTradeService pomTradeService;
    private final MIMServiceService mimService;
    private final TradeService tradeService;
    private final SSMDirectSendGiftCardDAO dao;
    private final ApplicationProperties properties;

    public String getGiftcardSendId() throws Exception {
        return simIndexService.getIndex(SIMIndex.SSM_GIFTCARD_SEND_LIST);
    }

    public String getGiftcardChangeId() throws Exception {
        return simIndexService.getIndex(SIMIndex.SSM_GIFTCARD_HIS);
    }

    public String getGiftcardSendPastId() throws Exception {
        return simIndexService.getIndex(SIMIndex.SSM_GIFTCARD_SEND_HIS);
    }

    public List<SSMSendInfo> getSendInfoList(SSMSendInfo sendInfoParam, TradeTp tradeTp) {
        if (TradeTp.SEND_DIRECT.getCode().equals(tradeTp.getCode())) {
            return dao.selectSendInfoList(sendInfoParam);
        } else {
            return dao.selectGoodsSendInfoList(sendInfoParam);
        }
    }

    public SSMGiftCardHistory getLastExpiryInfo(String sendId) throws Exception {
        List<SSMGiftCardHistory> giftCardHistoryList = dao.selectGiftCardHistoryList(sendId);

        SSMGiftCardHistory giftCardHistory = giftCardHistoryList.stream()
                .sorted(Comparator.comparing(SSMGiftCardHistory::getChangeId).reversed())
                .limit(1)
                .collect(Collectors.toList())
                .get(0);

        giftCardHistory.setReceiverPhone(AES256Util.decode(giftCardHistory.getReceiverPhone()));

        return giftCardHistory;
    }

    public long insertGiftCardSendHistory(SSMSendHistory giftCardSendHistory) {
        return dao.insertGiftCardSendHistory(giftCardSendHistory);
    }

    public String insertGiftCardExpiryHistory(String sendId, String expiryDt) throws Exception {
        String changeId = getGiftcardChangeId();

        SSMGiftCardHistory giftCardHistory = SSMGiftCardHistory.builder()
                .changeId(changeId)
                .sendId(sendId)
                .expiryDt(expiryDt)
                .build();

        dao.insertGiftCardExpiryHistory(giftCardHistory);

        return changeId;
    }

    public void updateGiftCardNo(SSMSendInfo sendInfo) {
        dao.updateGiftCardNo(sendInfo);
    }

    /**
     * 상품권 직발송 (직접발송, 수기발송)
     *
     * @param groupNo
     * @return
     */
    public int sendDirectGiftCard(int groupNo, TradeTp tradeTp) {
        int sendingCounts;
        FormTp formTp = tradeTp == TradeTp.SEND_DIRECT ? FormTp.SEND_DIRECT : FormTp.SEND_GOODS;

        try {
            SSMSendInfo sendInfoParam = createSendInfoParams(null, groupNo);
            List<SSMSendInfo> sendInfoList = getSendInfoList(sendInfoParam, tradeTp);

            for (SSMSendInfo sendInfo : sendInfoList) {
                sendInfo.setReceiverPhone(AES256Util.decode(sendInfo.getReceiverPhone()));
                Trade trade = createSendTrade(sendInfo, tradeTp);
                tradeService.tradeSales(trade, formTp);
            }

            sendingCounts = sendInfoList.size();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return sendingCounts;
    }

    /**
     * 상품권 수기발송
     *
     * @param sendGoodsRequest
     * @return
     */
    public void sendDirectGiftCard(SendGoodsRequest sendGoodsRequest) {
        SSMSendInfo sendInfo = dao.selectSendGoodsByGoodsId(sendGoodsRequest.getGoodsId(), properties.getSystemMode());
        sendInfo.setReceiverPhone(sendGoodsRequest.getReceiverPhone());
        sendInfo.setReceiverNm(sendGoodsRequest.getReceiverNm());
        try {
            Trade trade = createSendTrade(sendInfo, TradeTp.SALES);
            tradeService.tradeSales(trade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public SSMSendInfo createSendInfoParams(String sendId, int groupNo) {
        return SSMSendInfo.builder()
                .sendId(sendId)
                .groupNo(groupNo)
                .accessFl(properties.getSystemMode())
                .build();
    }

    public Trade createSendTrade(SSMSendInfo ssmSendInfo, TradeTp tradeTp) throws Exception {
        MIMService channelService = mimService.getMIMServiceByChannelGb(ChannelGb.PLATFOS.getCode());
        TradeSeqTp tradeSeqTp = TradeSeqTp.ORDER_NO;
        String orderNo = pomTradeService.createTradeSeqNo(tradeSeqTp).getTradeSeqNo();
        String tradeDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        GWBuyer buyer = GWBuyer.builder()
                .name(ssmSendInfo.getReceiverNm())
                .phoneNo(ssmSendInfo.getCenterTel())
                .build();
        GWReceiver receiver = GWReceiver.builder()
                .name(ssmSendInfo.getReceiverNm())
                .phoneNo(ssmSendInfo.getReceiverPhone())
                .build();

        Trade trade = Trade.builder()
                .serviceId(channelService.getServiceId())
                .supplyServiceId(ssmSendInfo.getServiceId())
                .goodsId(ssmSendInfo.getGoodsId())
                .serviceNm(ssmSendInfo.getServiceNm())
                .centerTel(ssmSendInfo.getCenterTel())
                .goodsNm(ssmSendInfo.getGoodsNm())
                .goodsImage(ssmSendInfo.getGoodsImage())
                .goodsInfo(ssmSendInfo.getGoodsInfo())
                .exchangePlace(ssmSendInfo.getExchangePlace())
                .orderNo(orderNo)
                .detailNo(orderNo)
                .orderPrice(ssmSendInfo.getSalePrice().intValue())
                .orderDate(tradeDate)
                .orderCnt(1) // TODO: 상품권 수량 기능 추가시 작업 필요
                .buyer(buyer)
                .receiver(receiver)
                .giftCardTp(GiftCardTp.findByCode(ssmSendInfo.getGiftcardTp()))
                .tradeTp(tradeTp)
                .expiryGb(ssmSendInfo.getExpiryGb())
                .retailPrice(ssmSendInfo.getRetailPrice().intValue())
                .build();

        POMTradeCreateResult result = tradeService.insertTrade(trade);
        if (result.getResultCode().equals("0000")) {
            trade.setTradeId(result.getPomTrade().getTradeId());
        }

        if (result.getResultCode().equals("0001")) {
            throw new TradeException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, result.getResultMessage());
        } else if (result.getResultCode().equals("0002")) {
            throw new TradeException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, result.getResultMessage());
        } else if (result.getResultCode().equals("0003")) {
            throw new TradeException(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, result.getResultMessage());
        } else if (!result.getResultCode().equals("0000")) {
            throw new TradeException(ResponseCode.FAIL, result.getResultMessage());
        }

        return trade;
    }
}
