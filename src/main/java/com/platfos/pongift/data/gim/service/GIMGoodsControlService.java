package com.platfos.pongift.data.gim.service;

import com.platfos.pongift.data.gim.dao.GIMGoodsControlDAO;
import com.platfos.pongift.data.gim.model.GIMGoodsControl;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.definition.ProcessSt;
import com.platfos.pongift.definition.SIMIndex;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GIMGoodsControlService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    GIMGoodsControlDAO dao;

    public GIMGoodsControl getBeforeProcessGIMGoodsControl(){
        return dao.selectBeforeProcessGIMGoodsControl();
    }
    public List<GIMGoodsControl> getGIMGoodsControls(String goodsId, ProcessSt... processSts) {
        List<String> processStList = new ArrayList<>();
        for(ProcessSt processSt : processSts){
            processStList.add(processSt.getCode());
        }
        return dao.selectGIMGoodsControlListByGroupIdAndProcessSts(goodsId, processStList);
    }
    @Transactional
    public long insertGIMGoodsControls(List<GIMGoodsControl> gimGoodsControls) throws Exception {
        long rs = 0;
        for(GIMGoodsControl gimGoodsControl : gimGoodsControls){
            rs += insertGIMGoodsControl(gimGoodsControl);
        }
        return rs;
    }
    @Transactional
    public long insertGIMGoodsControl(GIMGoodsControl gimGoodsControl) throws Exception {
        String controlId = simIndexService.getIndex(SIMIndex.GIM_GOODS_CONTROL);
        gimGoodsControl.setControlId(controlId);

        long rs = dao.insertGIMGoodsControl(gimGoodsControl);
        insertGIMGoodsControlHis(controlId);

        return rs;
    }
    @Transactional
    public long insertGIMGoodsControlHis(String controlId) throws Exception{
        String recordId = simIndexService.getIndex(SIMIndex.GIM_CONTROL_HIS);
        return dao.insertGIMGoodsControlHis(recordId, controlId);
    }
    @Transactional
    public int updateGIMGoodsControlProcessSt(String controlId, ProcessSt processSt) throws Exception {
        int rs = dao.updateGIMGoodsControlProcessSt(controlId, processSt.getCode());
        insertGIMGoodsControlHis(controlId);
        return rs;
    }
    @Transactional
    public int deleteGIMGoodsControl(String controlId) throws Exception {
        return dao.deleteGIMGoodsControl(controlId);
    }

    public String getGoodsIdForScheduler() {
        return dao.selectGoodsIdForScheduler();
    }

    public List<GIMGoodsControl> getGIMGoodsControlsForScheduler(String goodsId) {
        return dao.selectGIMGoodsControlForScheduler(goodsId);
    }
    @Transactional
    public int updateGIMGoodsControlProcessStForScheduler(String goodsId, ProcessSt processSt) throws Exception {
        return dao.updateGIMGoodsControlProcessStForScheduler(goodsId, processSt.getCode());
    }
    @Transactional
    public long insertGIMGoodsControlHisForScheduler(String goodsId, String channelGb, String processSt) throws Exception {
        return dao.insertGIMGoodsControlHisForScheduler(goodsId, channelGb, processSt);
    }
}
