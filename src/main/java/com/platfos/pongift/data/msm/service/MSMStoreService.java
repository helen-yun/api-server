package com.platfos.pongift.data.msm.service;

import com.platfos.pongift.attach.service.AttachFileService;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.msm.dao.MSMStoreDAO;
import com.platfos.pongift.data.msm.model.MSMStore;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MSMStoreService {
    @Autowired
    ApplicationProperties properties;

    @Autowired
    SIMIndexService simIndexService;
    @Autowired
    AttachFileService fileService;

    @Autowired
    MSMStoreDAO dao;

    @Autowired
    SIMCodeService simCodeService;

    public MSMStore getMSMStore(String storeId) {
        MSMStore store = dao.selectMSMStore(storeId, properties.getSystemMode());
        return store;
    }

    public String insertMSMStore(MSMStore store) throws Exception {
        String storeId = simIndexService.getIndex(SIMIndex.MSM_STORE);
        store.setStoreId(storeId);
        long rs = dao.insertMSMStore(store);

        if(rs>0) {
            fileService.uploadAttachFileByData(store);
            return storeId;
        }
        else return null;
    }

    public int updateMSMStore(MSMStore store) throws Exception {
        int rs = dao.updateMSMStore(store);

        if(rs>0) fileService.uploadAttachFileByData(store);

        return rs;
    }
}
