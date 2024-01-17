package com.platfos.pongift.data.gim.service;

import com.platfos.pongift.data.gim.dao.GIMChannelInfoDAO;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class GIMChannelInfoService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    GIMChannelInfoDAO dao;

    public GIMChannelInfo getGIMChannelInfo(String routeId, String useFl) {
        GIMChannelInfo channelInfo = dao.selectGIMChannelInfo(routeId, useFl);
        return channelInfo;
    }

    public GIMChannelInfo getGIMChannelInfo(String goodsId, String channelGb, String useFl) {
        GIMChannelInfo channelInfo = dao.selectGIMChannelInfoByGoodsIdAndChannelGb(goodsId, channelGb, useFl);
        return channelInfo;
    }

    public List<GIMChannelInfo> getGIMChannelInfoList(String goodsId, String useFl) {
        List<GIMChannelInfo> channelInfoList = dao.selectGIMChannelInfoListByGoodsId(goodsId, useFl);
        return channelInfoList;
    }
    public List<GIMChannelInfo> getGIMChannelInfoExpirationExhibitEndList() {
        return dao.selectGIMChannelInfoExpirationExhibitEnd();
    }
    public GIMChannelInfo selectGIMChannelInfoByChannelIdAndChannelGb(String channelId, String channelGb, String useFl) {
        GIMChannelInfo channelInfo = dao.selectGIMChannelInfoByChannelIdAndChannelGb(channelId, channelGb, useFl);
        return channelInfo;
    }
    @Transactional
    public String insertGIMChannelInfo(GIMChannelInfo channelInfo) throws Exception {
        String routeId = simIndexService.getIndex(SIMIndex.GIM_CHANNEL_INFO);
        channelInfo.setRouteId(routeId);
        long rs = dao.insertGIMChannelInfo(channelInfo);

        if(rs>0) {
            return routeId;
        }
        else return null;
    }
    @Transactional
    public int updateGIMChannelInfoExhibit(GIMChannelInfo channelInfo) throws Exception {
        int rs = dao.updateGIMChannelInfoExhibit(channelInfo);
        return rs;
    }
    @Transactional
    public int updateGIMChannelInfoChannelId(GIMChannelInfo channelInfo) throws Exception {
        int rs = dao.updateGIMChannelInfoChannelId(channelInfo);
        return rs;
    }
    @Transactional
    public int updateGIMChannelInfoUseFl(GIMChannelInfo channelInfo) throws Exception {
        int rs = dao.updateGIMChannelInfoUseFl(channelInfo);
        return rs;
    }

    public int deleteGIMChannelInfo(String routeId) {
        return dao.deleteGIMChannelInfo(routeId);
    }

    public int deleteGIMChannelInfoByGoodsId(String goodsId) {
        return dao.deleteGIMChannelInfoByGoodsId(goodsId);
    }

    public int deleteGIMChannelInfoByGoodsIdAndChannelGb(String goodsId, String channelGb) {
        return dao.deleteGIMChannelInfoByGoodsIdAndChannelGb(goodsId, channelGb);
    }
}
