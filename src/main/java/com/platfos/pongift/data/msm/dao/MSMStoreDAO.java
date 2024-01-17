package com.platfos.pongift.data.msm.dao;

import com.platfos.pongift.data.msm.model.MSMStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MSMStoreDAO {
    MSMStore selectMSMStore(@Param("storeId") String storeId, @Param("accessFl") String accessFl);
    long insertMSMStore(MSMStore store);
    int updateMSMStore(MSMStore store);
}
