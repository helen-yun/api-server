package com.platfos.pongift.data.gim.dao;

import com.platfos.pongift.data.gim.model.GIMExhibitInfo;
import com.platfos.pongift.data.gim.model.GIMGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GIMExhibitInfoDAO {
    List<GIMExhibitInfo> selectGIMExhibitInfoListByGoodsCategoryAndChannelGb(@Param("goodsCategory") String goodsCategory, @Param("channelGb") String channelGb);
}
