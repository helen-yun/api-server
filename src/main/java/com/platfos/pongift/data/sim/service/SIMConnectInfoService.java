package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.mim.dao.MIMServiceDAO;
import com.platfos.pongift.data.sim.dao.SIMConnectInfoDAO;
import com.platfos.pongift.data.sim.model.SIMConnectInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SIMConnectInfoService {
    @Autowired
    SIMConnectInfoDAO dao;

    public List<SIMConnectInfo> getSIMConnectInfoList(String accessIp) {
        return dao.selectSIMConnectInfoListByAccessIp(accessIp);
    }

    @Cacheable(value="SIMConnectInfoCache", key="#serviceId.concat(#accessIp)")
    public List<SIMConnectInfo> getSIMConnectInfoList(String serviceId, String accessIp) {
        return dao.selectSIMConnectInfoListByServiceIdAndAccessIp(serviceId, accessIp);
    }
}
