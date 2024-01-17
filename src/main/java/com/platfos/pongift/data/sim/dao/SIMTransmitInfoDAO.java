package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.GoodsSupplyInfoForAlimTalk;
import com.platfos.pongift.data.sim.model.SIMTransmitForAlimTalk;
import com.platfos.pongift.data.sim.model.SIMTransmitInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SIMTransmitInfoDAO {
    SIMTransmitInfo selectSIMTransmitInfo(@Param("transmitId") String transmitId, @Param("timeOver") int timeOver);
    /** 알림톡 전송 요청 정보 조회 **/
    SIMTransmitForAlimTalk selectSIMTransmitForAlimTalk(@Param("transmitId") String transmitId, @Param("accessFl") String accessFl);
    GoodsSupplyInfoForAlimTalk selectGoodsSupplyInfoByAlimTalk(SIMTransmitForAlimTalk alimTalk);
    long insertSIMTransmitInfo(SIMTransmitInfo transmitInfo);
    int updateSIMTransmitInfo(SIMTransmitInfo transmitInfo);
}