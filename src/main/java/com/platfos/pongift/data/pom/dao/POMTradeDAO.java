package com.platfos.pongift.data.pom.dao;

import com.platfos.pongift.data.pom.model.POMTrade;
import com.platfos.pongift.data.pom.model.POMTradeSeqNo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Mapper
@Repository
public interface POMTradeDAO {
    HashMap<String, String> callTradeListReg01Sp(HashMap<String, String> paramMap);
    POMTrade selectPOMTrade(@Param("tradeId") String tradeId);
    int countPOMTrade(POMTrade pomTrade);
    void createTradeSeqNo(POMTradeSeqNo param);
}
