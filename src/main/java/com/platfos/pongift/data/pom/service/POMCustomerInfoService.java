package com.platfos.pongift.data.pom.service;

import com.platfos.pongift.data.pom.dao.POMCustomerInfoDAO;
import com.platfos.pongift.data.pom.dao.POMTradeGiftDAO;
import com.platfos.pongift.data.pom.model.POMCustomerInfo;
import com.platfos.pongift.data.pom.model.POMTradeGift;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.definition.SIMIndex;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class POMCustomerInfoService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    POMCustomerInfoDAO dao;

    public POMCustomerInfo getPOMCustomerInfoByProductId(String productId){
        return dao.selectPOMCustomerInfoByProductId(productId);
    }
    public String insertPOMCustomerInfo(POMCustomerInfo pomCustomerInfo) throws Exception {
        String customerId = simIndexService.getIndex(SIMIndex.POM_CUSTOMER_INFO);
        pomCustomerInfo.setCustomerId(customerId);
        long rs = dao.insertPOMCustomerInfo(pomCustomerInfo);

        if(rs>0) {
            return customerId;
        }
        else return null;
    }

    public int updatePOMCustomerInfo(String customerId, String buyerNm, String buyerPhone, String receiverNm, String receiverPhone) throws Exception {
        return dao.updatePOMCustomerInfo(customerId, buyerNm, buyerPhone, receiverNm, receiverPhone);
    }
}
