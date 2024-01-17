package com.platfos.pongift.data.pom.service;

import com.platfos.pongift.data.pom.dao.POMDispatchInfoDAO;
import com.platfos.pongift.data.pom.dao.POMTradeGiftDAO;
import com.platfos.pongift.data.pom.model.POMDispatchInfo;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.definition.SIMIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class POMDispatchInfoService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    POMDispatchInfoDAO dao;

    public int getResendCountByProductId(String productId){
        return dao.selectResendCountByProductId(productId);
    }
    public int getExpiredNoticeByProductId(String productId){
        return dao.selectExpiredNoticeCountByProductId(productId);
    }
    public String insertPOMDispatchInfo(POMDispatchInfo pomDispatchInfo) throws Exception {
        String dispatchId = simIndexService.getIndex(SIMIndex.POM_DISPATCH_INFO);
        pomDispatchInfo.setDispatchId(dispatchId);
        long rs = dao.insertPOMDispatchInfo(pomDispatchInfo);

        if(rs>0) {
            return dispatchId;
        }
        else return null;
    }

}
