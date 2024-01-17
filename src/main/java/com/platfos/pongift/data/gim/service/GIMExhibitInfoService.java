package com.platfos.pongift.data.gim.service;

import com.platfos.pongift.data.gim.dao.GIMExhibitInfoDAO;
import com.platfos.pongift.data.gim.model.GIMExhibitInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GIMExhibitInfoService {
    @Autowired
    GIMExhibitInfoDAO dao;

    public List<GIMExhibitInfo> getGIMExhibitInfos(String goodsCategory, String channelGb) {
        return dao.selectGIMExhibitInfoListByGoodsCategoryAndChannelGb(goodsCategory, channelGb);
    }
}
