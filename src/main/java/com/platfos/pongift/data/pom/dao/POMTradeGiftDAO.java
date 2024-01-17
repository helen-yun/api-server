package com.platfos.pongift.data.pom.dao;

import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Mapper
@Repository
public interface POMTradeGiftDAO {
    long insertPOMTradeGift(POMTradeGift pomTradeGift);

    POMTradeGift selectPOMTradeGiftByLedgerIdAndTradeTp(@Param("ledgerId") String ledgerId, @Param("tradeTp") String tradeTp);

    POMTradeGift selectPOMTradeGift(POMTradeGift pomTradeGift);
}
