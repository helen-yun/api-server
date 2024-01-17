package com.platfos.pongift.data.cim.service;

import com.platfos.pongift.data.cim.dao.CIMLedgerDAO;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.model.CIMLedgerHis;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.definition.SIMIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class CIMLedgerService {
    private final SIMIndexService simIndexService;
    private final CIMLedgerDAO cimLedgerDAO;

    public CIMLedger getCIMLedger(String ledgerId) {
        return cimLedgerDAO.selectCIMLedger(ledgerId);
    }

    public CIMLedger getCIMLedgerByTradeList(String tradeTp, String orderNo, String serviceId) {
        return cimLedgerDAO.selectCIMLedgerByTradeList(tradeTp, orderNo, serviceId);
    }

    public CIMLedger getCIMLedgerByGiftNo(String giftNo) {
        return cimLedgerDAO.selectCIMLedgerByGiftNo(giftNo);
    }

    public CIMLedger getCIMLedgerByAES256GiftNo(String giftNo) {
        return cimLedgerDAO.selectCIMLedgerByAES256GiftNo(giftNo);
    }

    public List<CIMLedger> selectCIMLedgerList(String tradeId) {
        return cimLedgerDAO.selectCIMLedgerListByTradeId(tradeId);
    }

    public List<CIMLedger> selectCIMLedgerList(String tradeTp, String orderNo, String detailNo) {
        return cimLedgerDAO.selectCIMLedgerListByTradeTpAndOrderNoAndDetailNo(tradeTp, orderNo, detailNo);
    }

    public List<CIMLedger> selectExpiredNoticeCIMLedgerList() {
        return cimLedgerDAO.selectCIMLedgerExpiredNotice();
    }

    public String insertCIMLedger(CIMLedger cimLedger) throws Exception {
        String ledgerId = simIndexService.getIndex(SIMIndex.CIM_LEDGER);
        cimLedger.setLedgerId(ledgerId);
        long rs = cimLedgerDAO.insertCIMLedger(cimLedger);

        if (rs > 0) {
            return ledgerId;
        } else return null;
    }

    @Transactional
    public String insertCIMLedgerHis(CIMLedgerHis cimLedgerHis) throws Exception {
        String modifyId = simIndexService.getIndex(SIMIndex.CIM_LEDGER_HIS);
        cimLedgerHis.setModifyId(modifyId);
        long rs = cimLedgerDAO.insertCIMLedgerHis(cimLedgerHis);

        if (rs > 0) {
            return modifyId;
        } else return null;
    }

    @Transactional
    public int updateCIMLedger(String ledgerId, String giftNo, String bareNo, String expiryDt, String giftSt) {
        return cimLedgerDAO.updateCIMLedger(ledgerId, giftNo, bareNo, expiryDt, giftSt);
    }
}
