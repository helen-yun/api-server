package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMReplyCodeDAO;
import com.platfos.pongift.definition.SIMReplyCodeAPIGB;
import com.platfos.pongift.data.sim.model.SIMReplyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SIMReplyCodeService {
    @Autowired
    SIMReplyCodeDAO dao;

    @CacheEvict(value = "SIMReplyCodeCache", allEntries = true)
    public void refreshSIMReplyCodeCache(){}

    @Cacheable(value="SIMReplyCodeCache")
    public HashMap<String, SIMReplyCode> getReplyCodeResources(){
        String apiGb = SIMReplyCodeAPIGB.API.getCode();
        HashMap<String, SIMReplyCode> replyCodeResources = new HashMap<>();
        List<String> applyKeys = dao.selectApplyKeyList(apiGb);

        for(String applyKey : applyKeys){
            SIMReplyCode aimMessageInfos = dao.selectSIMReplyCodeByApplyKey(apiGb, applyKey);
            replyCodeResources.put(applyKey, aimMessageInfos);
        }

        return replyCodeResources;
    }

    public List<SIMReplyCode> getSIMReplyCodeList(SIMReplyCodeAPIGB apigb){
        return dao.selectSIMReplyCodeList(apigb.getCode());
    }
}
