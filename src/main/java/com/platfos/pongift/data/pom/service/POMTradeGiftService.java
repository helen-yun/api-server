package com.platfos.pongift.data.pom.service;

import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.pom.dao.POMTradeGiftDAO;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.definition.TradeTp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class POMTradeGiftService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    POMTradeGiftDAO dao;

    @Transactional
    public String insertPOMTradeGift(POMTradeGift pomTradeGift) throws Exception {
        String productId = simIndexService.getIndex(SIMIndex.POM_TRADE_GIFT);
        pomTradeGift.setProductId(productId);
        long rs = dao.insertPOMTradeGift(pomTradeGift);

        if (rs > 0) {
            return productId;
        } else return null;
    }

    public POMTradeGift selectPOMTradeGift(POMTradeGift pomTradeGift) {
        return dao.selectPOMTradeGift(pomTradeGift);
    }

    public POMTradeGift getPOMTradeGiftByLedgerIdAndTradeTp(String ledgerId, TradeTp tradeTp) {
        return dao.selectPOMTradeGiftByLedgerIdAndTradeTp(ledgerId, tradeTp.getCode());
    }

}
