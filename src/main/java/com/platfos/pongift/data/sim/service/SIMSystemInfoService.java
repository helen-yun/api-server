package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.config.properties.WideshotSystemInfo;
import com.platfos.pongift.data.sim.dao.SIMSystemInfoDAO;
import com.platfos.pongift.data.sim.model.SIMSystemInfo;
import com.platfos.pongift.definition.SystemKey;
import com.platfos.pongift.definition.SystemMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SIMSystemInfoService {
    @Autowired
    ApplicationProperties properties;

    @Autowired
    SIMSystemInfoDAO dao;

    @CacheEvict(value = "SIMSystemInfoCache", allEntries = true)
    public void refreshSIMSystemInfoCache(){}

    public List<SIMSystemInfo> getSIMSystemInfoList(){
        return dao.selectSIMSystemInfoList();
    }
    public SIMSystemInfo get(SystemKey key){
        if(StringUtils.isEmpty(properties.getSystemMode())) return null;
        if(key == null) return null;
        return get(key.getKey());
    }
    public String getValue(SystemKey key){
        SIMSystemInfo systemInfo = get(key);
        if(systemInfo == null) return null;
        return systemInfo.getSystemNm();
    }
    public SIMSystemInfo get(String key){
        if(StringUtils.isEmpty(properties.getSystemMode())) return null;
        if(StringUtils.isEmpty(key)) return null;

        List<SIMSystemInfo> list = getSIMSystemInfoList();
        for(SIMSystemInfo simSystemInfo : list){
            if(simSystemInfo.getAccessFl().equals(properties.getSystemMode()) & simSystemInfo.getApplyKey().equals(key)){
                return simSystemInfo;
            }
        }
        return null;
    }
    public String getValue(String key){
        SIMSystemInfo systemInfo = get(key);
        if(systemInfo == null) return null;
        return systemInfo.getSystemNm();
    }

    public WideshotSystemInfo getWideShotInfo(){
        String wideshotMode = properties.getWideshotMode();
        SystemMode systemMode = SystemMode.findByCode(wideshotMode);
//        String apiName = (systemMode==SystemMode.PRODUCT)?"api":"dev-api"; // dev-api 사용 안함?
        String apiName = "api";
        String baseKey = "wideshot."+apiName+".";

        SIMSystemInfo host = get(baseKey+"host");
        SIMSystemInfo key = get(baseKey+"key");
        SIMSystemInfo callback = get(baseKey+"callback");
        /** 알림톡 플러스 친구 채널명 **/
        SIMSystemInfo plusFriendId = get("wideshot.talk.plus-friendId");
        /** 알림톡 발신 프로필 키 **/
        SIMSystemInfo senderKey = get("wideshot.talk.sender-key");

        return WideshotSystemInfo.builder()
                .host(((host != null)?host.getSystemNm():null))
                .key(((key != null)?key.getSystemNm():null))
                .callback(((callback != null)?callback.getSystemNm():null))
                .plusFriendId(((plusFriendId != null)?plusFriendId.getSystemNm():null))
                .senderKey(((senderKey != null)?senderKey.getSystemNm():null))
                .build();
    }

    public String getGatewayHost(String channelGb){
        String baseKey = "gateway."+channelGb+".host";
        return getValue(baseKey);
    }

    public boolean existGateway(String channelGb, String gatewayUrl, String method){
        if(StringUtils.isEmpty(channelGb)) return false;
        if(StringUtils.isEmpty(gatewayUrl)) return false;
        if(StringUtils.isEmpty(method)) return false;

        String methodTp = null;

        if(method.equalsIgnoreCase("get")) methodTp = "01";
        else if(method.equalsIgnoreCase("post")) methodTp = "02";
        else if(method.equalsIgnoreCase("put")) methodTp = "03";
        else if(method.equalsIgnoreCase("delete")) methodTp = "04";

        if(StringUtils.isEmpty(methodTp)) return false;

        return dao.existGateway(properties.getSystemMode(), channelGb, gatewayUrl, methodTp);
    }
}
