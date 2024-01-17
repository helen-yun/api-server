package com.platfos.pongift.data.gim.dao;

import com.platfos.pongift.data.gim.model.GIMGoods;
import com.platfos.pongift.data.gim.model.GIMGoodsControl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GIMGoodsControlDAO {
    GIMGoodsControl selectBeforeProcessGIMGoodsControl();
    List<GIMGoodsControl> selectGIMGoodsControlList(@Param("goodsId") String goodsId, @Param("processSt") String processSt);
    List<GIMGoodsControl> selectGIMGoodsControlListByGroupIdAndProcessSts(@Param("goodsId") String goodsId, @Param("processSts") List<String> processSts);
    long insertGIMGoodsControl(GIMGoodsControl gimGoodsControl);
    long insertGIMGoodsControlHis(@Param("recordId") String recordId, @Param("controlId") String controlId);
    int updateGIMGoodsControlProcessSt(@Param("controlId") String controlId, @Param("processSt") String processSt);
    int updateGIMGoodsControlExecuteCnt(@Param("controlId") String controlId, @Param("executeCnt") int executeCnt);
    int deleteGIMGoodsControl(@Param("controlId") String controlId);

    String selectGoodsIdForScheduler();
    List<GIMGoodsControl> selectGIMGoodsControlForScheduler(@Param("goodsId") String goodsId);
    int updateGIMGoodsControlProcessStForScheduler(@Param("goodsId") String goodsId, @Param("processSt") String processSt);
    long insertGIMGoodsControlHisForScheduler(@Param("goodsId") String goodsId, @Param("channelGb") String channelGb, @Param("processSt") String processSt);
}
