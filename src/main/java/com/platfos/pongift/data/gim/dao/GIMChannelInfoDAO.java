package com.platfos.pongift.data.gim.dao;

import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.gim.model.GIMGoodsSub;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GIMChannelInfoDAO {
    GIMChannelInfo selectGIMChannelInfo(@Param("routeId") String routeId, @Param("useFl") String useFl);
    GIMChannelInfo selectGIMChannelInfoByGoodsIdAndChannelGb(@Param("goodsId") String goodsId, @Param("channelGb") String channelGb, @Param("useFl") String useFl);
    List<GIMChannelInfo> selectGIMChannelInfoListByGoodsId(@Param("goodsId") String goodsId, @Param("useFl") String useFl);
    List<GIMChannelInfo> selectGIMChannelInfoExpirationExhibitEnd();
    GIMChannelInfo selectGIMChannelInfoByChannelIdAndChannelGb(@Param("channelId") String channelId, @Param("channelGb") String channelGb, @Param("useFl") String useFl);
    long insertGIMChannelInfo(GIMChannelInfo gimChannelInfo);
    int updateGIMChannelInfoExhibit(GIMChannelInfo gimChannelInfo);
    int updateGIMChannelInfoChannelId(GIMChannelInfo gimChannelInfo);
    int updateGIMChannelInfoUseFl(GIMChannelInfo gimChannelInfo);
    int deleteGIMChannelInfo(@Param("routeId") String routeId);
    int deleteGIMChannelInfoByGoodsId(@Param("goodsId") String goodsId);
    int deleteGIMChannelInfoByGoodsIdAndChannelGb(@Param("goodsId") String goodsId, @Param("channelGb") String channelGb);
}
