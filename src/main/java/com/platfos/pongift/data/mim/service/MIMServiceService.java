package com.platfos.pongift.data.mim.service;

import com.platfos.pongift.data.mim.dao.MIMServiceDAO;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.definition.MIMServiceSt;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MIMServiceService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    MIMServiceDAO dao;

    @Autowired
    SIMCodeService simCodeService;

    public MIMService getMIMService(String serviceId) {
        MIMService service = dao.selectMIMService(serviceId);
        return service;
    }

    public MIMService getMIMServiceByChannelGb(String channelGb) {
        MIMService service = dao.selectMIMServiceListByChannelGb(channelGb);
        return service;
    }

    public List<MIMService> selectMIMServiceList(String agencyId){
        return dao.selectMIMServiceListByAgencyId(agencyId);
    }

    public String getChannelGbByServiceId(String serviceId){
        return dao.selectChannelGbByServiceId(serviceId);
    }

    public String insertMIMService(MIMService service) throws Exception {
        String serviceId = simIndexService.getIndex(SIMIndex.MIM_SERVICE);
        service.setServiceId(serviceId);
        long rs = dao.insertMIMService(service);

        if(rs>0) {
            return serviceId;
        }
        else return null;
    }

    public int updateMIMService(MIMService service) {
        int rs = dao.updateMIMService(service);
        return rs;
    }

    public int updateMIMServiceSt(String serviceId, MIMServiceSt serviceSt){
        return dao.updateMIMServiceSt(serviceId, serviceSt.getCode());
    }
}
