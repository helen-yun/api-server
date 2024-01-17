package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.sim.dao.SIMTransmitInfoDAO;
import com.platfos.pongift.data.sim.model.GoodsSupplyInfoForAlimTalk;
import com.platfos.pongift.data.sim.model.SIMTransmitForAlimTalk;
import com.platfos.pongift.data.sim.model.SIMTransmitInfo;
import com.platfos.pongift.definition.SIMIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SIMTransmitInfoService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    SIMTransmitInfoDAO dao;

    @Autowired
    ApplicationProperties properties;

    public String createTransmitId() throws Exception {
        return simIndexService.getIndex(SIMIndex.SIM_TRANSMIT_INFO);
    }

    public SIMTransmitInfo getSIMTransmitInfo(String transmitId){
        return dao.selectSIMTransmitInfo(transmitId, properties.getWideshotResultTimeOver());
    }

    /** 알림톡 전송 요청 정보 조회 **/
    public SIMTransmitForAlimTalk getSIMTransmitForAlimTalk(String transmitId){
        return dao.selectSIMTransmitForAlimTalk(transmitId, properties.getSystemMode());
    }

    /** 알림톡 전송 요청 정보 조회 (상품, 공급사 정보)**/
    public GoodsSupplyInfoForAlimTalk getGoodsSupplyInfoByAlimTalk(SIMTransmitForAlimTalk alimTalk){
        return dao.selectGoodsSupplyInfoByAlimTalk(alimTalk);
    }


    public long insertSIMTransmitInfo(SIMTransmitInfo simTransmitInfo){
        return dao.insertSIMTransmitInfo(simTransmitInfo);
    }

    public int updateSIMTransmitInfo(SIMTransmitInfo simTransmitInfo){
        return dao.updateSIMTransmitInfo(simTransmitInfo);
    }
}
