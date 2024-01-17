package com.platfos.pongift.data.msm.service;

import com.platfos.pongift.data.msm.dao.MSMOperateInfoDAO;
import com.platfos.pongift.data.msm.model.MSMOperateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MSMOperateInfoService {
    @Autowired
    MSMOperateInfoDAO dao;

    public List<MSMOperateInfo> getMSMOperateInfoList(String storeId) {
        return dao.selectMSMOperateInfoByStoreId(storeId);
    }

    public long insertMSMOperateInfos(List<MSMOperateInfo> operateInfos) throws Exception {
        return dao.insertMSMOperateInfos(operateInfos);
    }

    public int deleteMSMOperateInfo(String storeId) throws Exception {
        return dao.deleteMSMOperateInfoByStoreId(storeId);
    }
}
