package com.platfos.pongift.data.aim.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.aim.dao.AIMMessageInfoDAO;
import com.platfos.pongift.data.aim.model.AIMMessageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
public class AIMMessageInfoService {
    @Autowired
    private ApplicationProperties properties;

    @Autowired
    AIMMessageInfoDAO dao;

    @CacheEvict(value = "AIMMessageInfoListCache", allEntries = true)
    public void refreshAIMMessageInfoListCache(){}

    public List<AIMMessageInfo> getAIMMessageInfoList() {
        return dao.selectAIMMessageInfoList();
    }

    public HashMap<String, String> getMessageResources(){
        HashMap<String, String> messageResources = new HashMap<>();
        List<AIMMessageInfo> messages = dao.selectAIMMessageInfoList();
        for (AIMMessageInfo message : messages) {
            messageResources.put(makeMessageKey(message.getApplyKey(), message.getLanguageGb()), message.getMessageNm());
        }
        return messageResources;
    }

    public String getMessage(String applyKey, String lang){
        if(StringUtils.isEmpty(applyKey)) return null;
        if(StringUtils.isEmpty(lang)) return null;

        HashMap<String, String> messageResources = getMessageResources();
        if(messageResources != null && messageResources.size() > 0){
            String messageKey = makeMessageKey(applyKey, lang);
            String defMessageKey = makeMessageKey(applyKey, properties.getDefaultLanguage());

            if(messageResources.containsKey(messageKey)){
                return messageResources.get(messageKey);
            }else if(messageResources.containsKey(defMessageKey)){
                return messageResources.get(defMessageKey);
            }
        }
        return null;
    }

    private String makeMessageKey(String applyKey, String lang){
        return applyKey + "-" + lang.toUpperCase();
    }
}
