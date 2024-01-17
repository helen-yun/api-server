package com.platfos.pongift.data.gim.service;

import com.platfos.pongift.data.gim.dao.GIMGoodsSubDAO;
import com.platfos.pongift.data.gim.model.GIMGoodsSub;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GIMGoodsSubService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    GIMGoodsSubDAO dao;

    public GIMGoodsSub getGIMGoodsSub(String optionId) {
        GIMGoodsSub goodsSub = dao.selectGIMGoodsSub(optionId);
        return goodsSub;
    }

    public List<GIMGoodsSub> getGIMGoodsSubList(String goodsId) {
        List<GIMGoodsSub> goodsSubList = dao.selectGIMGoodsSubListByGoodsId(goodsId);
        return goodsSubList;
    }

    public String insertGIMGoodsSub(GIMGoodsSub goodsSub) throws Exception {
        String optionId = simIndexService.getIndex(SIMIndex.GIM_GOODS_SUB);
        goodsSub.setOptionId(optionId);
        long rs = dao.insertGIMGoodsSub(goodsSub);

        if(rs>0) {
            return optionId;
        }
        else return null;
    }

    public int updateGIMGoodsSub(GIMGoodsSub goodsSub) throws Exception {
        int rs = dao.updateGIMGoodsSub(goodsSub);
        return rs;
    }

    public int deleteGIMGoodsSub(String optionId) {
        return dao.deleteGIMGoodsSub(optionId);
    }

    public int deleteGIMGoodsSubByGoodsId(String goodsId) {
        return dao.deleteGIMGoodsSubByGoodsId(goodsId);
    }
}
