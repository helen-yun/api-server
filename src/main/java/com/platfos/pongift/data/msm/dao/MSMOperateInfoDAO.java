package com.platfos.pongift.data.msm.dao;

import com.platfos.pongift.data.msm.model.MSMOperateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MSMOperateInfoDAO {
    List<MSMOperateInfo> selectMSMOperateInfoByStoreId(@Param("storeId") String storeId);
    long insertMSMOperateInfos(@Param("list") List<MSMOperateInfo> list);
    int deleteMSMOperateInfoByStoreId(@Param("storeId") String storeId);
}
