package com.platfos.pongift.data.pom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.data.pom.dao.POMTradeDAO;
import com.platfos.pongift.data.pom.model.POMTrade;
import com.platfos.pongift.data.pom.model.POMTradeCreateResult;
import com.platfos.pongift.data.pom.model.POMTradeSeqNo;
import com.platfos.pongift.definition.TradeSeqTp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class POMTradeService {
    private final POMTradeDAO pomTradeDAO;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public POMTradeCreateResult createTrade(POMTrade pomTrade) throws JsonProcessingException {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("P_SERVICE_ID", pomTrade.getServiceId());
        paramMap.put("P_STORE_ID", pomTrade.getStoreId());
        paramMap.put("P_GOODS_ID", pomTrade.getGoodsId());
        paramMap.put("P_LEDGER_ID", pomTrade.getLedgerId());
        paramMap.put("P_TRADE_TP", pomTrade.getTradeTp());
        paramMap.put("P_ORDER_NO", pomTrade.getOrderNo());
        paramMap.put("P_DETAIL_NO", pomTrade.getDetailNo());
        paramMap.put("P_ORDER_AMT", String.valueOf(pomTrade.getOrderAmt()));
        paramMap.put("P_ORDER_DT", pomTrade.getOrderDt());
        paramMap.put("P_ORDER_TM", pomTrade.getOrderTm());
        paramMap.put("P_ORDER_CNT", String.valueOf(pomTrade.getOrderCnt()));
        paramMap.put("P_PAYMENT_GB", pomTrade.getPaymentGb());

        log.info("callTradeListReg01Sp Param: {} ", mapper.writeValueAsString(paramMap));
        pomTradeDAO.callTradeListReg01Sp(paramMap);

        pomTrade.setTradeId(paramMap.get("P_TRADE_ID"));
        pomTrade.setApprovalNo(paramMap.get("P_APPROVAL_NO"));

        POMTradeCreateResult result = POMTradeCreateResult.builder()
                .resultCode(paramMap.get("P_RESULT_CODE"))
                .resultMessage(paramMap.get("P_RESULT_MSG"))
                .pomTrade(pomTrade)
                .build();

        return result;
    }

    public POMTrade getPOMTrade(String tradeId) {
        return pomTradeDAO.selectPOMTrade(tradeId);
    }

    /**
     * @param tradeSeqTp 생성할 시퀀스 타입
     * @return pomTradeSeqNo
     */
    public POMTradeSeqNo createTradeSeqNo(TradeSeqTp tradeSeqTp) {
        POMTradeSeqNo pomTradeSeqNo = new POMTradeSeqNo();
        pomTradeSeqNo.setInputCd(tradeSeqTp.getCode());
        pomTradeDAO.createTradeSeqNo(pomTradeSeqNo);
        return pomTradeSeqNo;
    }

    /**
     * 거래유형 기준 카운팅
     * @param pomTrade
     * @return
     */
    public int countPOMTrade(POMTrade pomTrade) {
        return pomTradeDAO.countPOMTrade(pomTrade);
    }
}
