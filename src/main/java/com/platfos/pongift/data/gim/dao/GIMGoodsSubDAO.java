package com.platfos.pongift.data.gim.dao;

import com.platfos.pongift.data.gim.model.GIMGoodsSub;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GIMGoodsSubDAO {
    GIMGoodsSub selectGIMGoodsSub(@Param("optionId") String optionId);
    List<GIMGoodsSub> selectGIMGoodsSubListByGoodsId(@Param("goodsId") String goodsId);
    long insertGIMGoodsSub(GIMGoodsSub gimGoodsSub);
    int updateGIMGoodsSub(GIMGoodsSub gimGoodsSub);
    int deleteGIMGoodsSub(@Param("optionId") String optionId);
    int deleteGIMGoodsSubByGoodsId(@Param("goodsId") String goodsId);
}
