package com.platfos.pongift.data.gim.service;

import com.platfos.pongift.attach.service.AttachFileService;
import com.platfos.pongift.data.gim.dao.GIMGoodsDAO;
import com.platfos.pongift.definition.ExhibitSt;
import com.platfos.pongift.definition.ProcessSt;
import com.platfos.pongift.data.gim.model.GIMGoods;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.service.SIMIndexService;
import com.platfos.pongift.goods.model.GoodsChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class GIMGoodsService {
    @Autowired
    SIMIndexService simIndexService;
    @Autowired
    AttachFileService fileService;

    @Autowired
    GIMGoodsDAO dao;

    public GIMGoods getGIMGoods(String goodsId){
        return dao.selectGIMGoods(goodsId);
    }
    @Transactional
    public String insertGIMGoods(GIMGoods goods) throws Exception {
        String goodsId = simIndexService.getIndex(SIMIndex.GIM_GOODS);
        goods.setGoodsId(goodsId);
        long rs = dao.insertGIMGoods(goods);

        if(rs>0) {
            fileService.uploadAttachFileByData(goods);
            return goodsId;
        }
        else return null;
    }

    @Transactional
    public int updateGIMGoods(GIMGoods goods) throws Exception {
        int rs = dao.updateGIMGoods(goods);

        if(rs>0) fileService.uploadAttachFileByData(goods);

        return rs;
    }

    @Transactional
    public int updateGIMGoodsExhibitSt(String goodsId, ExhibitSt exhibitSt) {
        return dao.updateGIMGoodsExhibitSt(goodsId, exhibitSt.getCode());
    }

    @Transactional
    public int updateGIMGoodsProcessSt(String goodsId, ProcessSt processSt) {
        return dao.updateGIMGoodsProcessSt(goodsId, processSt.getCode());
    }

    @Transactional
    public int deleteGIMGoods(String goodsId) {
        return dao.deleteGIMGoods(goodsId);
    }

    public boolean existGIMGoodsByExhibitSt(String agencyId, ExhibitSt exhibitSt){
        return dao.existGIMGoodsByExhibitSt(agencyId, exhibitSt.getCode());
    }
    public List<GIMGoods> getGIMGoodsList(){
        return dao.selectGIMGoodsByExhibitSt();
    }

    @Transactional
    public void updateGIMGoodsStock(String channelGb, String goodsId, int orderCnt) {
        dao.updateGIMGoodsStock(channelGb, goodsId, orderCnt);
    }
    
    public GoodsChannel getGIMGoodsChannel(String channelGb, String goodsId) {
        return dao.selectGIMGoodsChannel(channelGb, goodsId);
    }

    @Transactional
    public void updateGIMGoodsStockAndSoldOut(String goodsId) {
        dao.updateGIMGoodsStockAndSoldOut(goodsId);
    }

    @Transactional
    public void updateGIMGoodsMarketStock(String channelGb, String goodsId) {
        dao.updateGIMGoodsMarketStock(channelGb, goodsId);
    }
}
