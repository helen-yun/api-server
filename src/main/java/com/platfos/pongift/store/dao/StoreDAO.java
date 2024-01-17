package com.platfos.pongift.store.dao;

import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.store.model.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StoreDAO {
    List<Store> selectStoreListByAgencyId(@Param("agencyId") String agencyId, @Param("accessFl") String accessFl);
    List<Store> selectOwnerStoreListByAgencyId(@Param("agencyId") String agencyId, @Param("accessFl") String accessFl);
    List<Store> selectStoreListByAgencyIdAndOwnedId(@Param("agencyId") String agencyId, @Param("ownedId") String ownedId, @Param("accessFl") String accessFl);
    Store selectStore(@Param("agencyId") String agencyId, @Param("storeId") String storeId, @Param("accessFl") String accessFl);
    Store selectStoreByGoodsId(@Param("goodsId") String goodsId);
}
