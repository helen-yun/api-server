package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMIndexDAO;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.exception.CreateInexException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SIMIndexService {
    private static final Logger logger = LoggerFactory.getLogger(SIMIndexService.class);
    @Autowired
    SIMIndexDAO dao;

    public String getIndex(SIMIndex simIndex) throws Exception {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("P_PK_CD", simIndex.getCode());
        dao.callIndexReg01Sp(paramMap);

        String resultCode = paramMap.get("P_RESULT_CODE");
        String resultMessage = paramMap.get("P_RESULT_MSG");
        String resultPK = paramMap.get("P_TABLE_PK");

        if(!StringUtils.isEmpty(resultCode) && resultCode.equals("0000")){
            logger.debug("create index success : " + resultPK);
            return resultPK;
        }

        throw new CreateInexException("create index fail : "+resultMessage+" ("+resultCode+")");
    }

    public String getPinNo(String pinRndNo, String pinTypeGb) throws Exception {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("PIN_RANDOM_NO", pinRndNo);
        paramMap.put("PIN_TYPE_GB", pinTypeGb);
        dao.callGiftCardReg01Sp(paramMap);

        String resultCode = paramMap.get("P_RESULT_CODE");
        String resultMessage = paramMap.get("P_RESULT_MSG");
        String resultPinNo = paramMap.get("P_PIN_NO");

        if(!StringUtils.isEmpty(resultCode) && resultCode.equals("0000")){
            logger.debug("create pin success");
            return resultPinNo;
        }

        throw new CreateInexException("create pin fail : "+resultMessage+" ("+resultCode+")");
    }
}
