package com.platfos.pongift.data.cim.dao;

import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.model.CIMLedgerHis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CIMLedgerDAO {
    CIMLedger selectCIMLedger(@Param("ledgerId") String ledgerId);
    CIMLedger selectCIMLedgerByGiftNo(@Param("giftNo") String giftNo);
    CIMLedger selectCIMLedgerByAES256GiftNo(@Param("giftNo") String giftNo);
    CIMLedger selectCIMLedgerByTradeList(@Param("tradeTp") String tradeTp, @Param("orderNo") String orderNo, @Param("serviceId") String serviceId);
    List<CIMLedger> selectCIMLedgerListByTradeId(@Param("tradeId") String tradeId);
    List<CIMLedger> selectCIMLedgerListByTradeTpAndOrderNoAndDetailNo(@Param("tradeTp") String tradeTp, @Param("orderNo") String orderNo, @Param("detailNo") String detailNo);
    List<CIMLedger> selectCIMLedgerExpiredNotice();
    long insertCIMLedger(CIMLedger cimLedger);
    long insertCIMLedgerHis(CIMLedgerHis cimLedgerHis);
    int updateCIMLedger(@Param("ledgerId") String ledgerId, @Param("giftNo") String giftNo, @Param("bareNo") String bareNo, @Param("expiryDt") String expiryDt, @Param("giftSt") String giftSt);
}
