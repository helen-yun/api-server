package com.platfos.pongift.data.gim.dao;

import com.platfos.pongift.data.gim.model.GIMGoods;
import com.platfos.pongift.data.msm.model.MSMStore;
import com.platfos.pongift.goods.model.GoodsChannel;
import com.platfos.pongift.goods.model.GoodsSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GIMGoodsDAO {
    GIMGoods selectGIMGoods(@Param("goodsId") String goodsId);
    List<GIMGoods> selectGIMGoodsByExhibitSt();
    boolean existGIMGoodsByExhibitSt(@Param("agencyId") String agencyId, @Param("exhibitSt") String exhibitSt);
    long insertGIMGoods(GIMGoods gimGoods);
    int updateGIMGoods(GIMGoods gimGoods);
    int updateGIMGoodsExhibitSt(@Param("goodsId") String goodsId, @Param("exhibitSt") String exhibitSt);
    int updateGIMGoodsProcessSt(@Param("goodsId") String goodsId, @Param("processSt") String processSt);
    int deleteGIMGoods(@Param("goodsId") String goodsId);
    
    void updateGIMGoodsStock(@Param("channelGb") String channelGb, @Param("goodsId") String goodsId, @Param("orderCnt") int orderCnt);
    void updateGIMGoodsStockAndSoldOut(@Param("goodsId") String goodsId);

    void updateGIMGoodsMarketStock(@Param("channelGb") String channelGb, @Param("goodsId") String goodsId);
    GoodsChannel selectGIMGoodsChannel(@Param("channelGb") String channelGb, @Param("goodsId") String goodsId);
}
