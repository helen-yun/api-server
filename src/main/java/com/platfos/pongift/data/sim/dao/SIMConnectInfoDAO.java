package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMConnectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SIMConnectInfoDAO {
    List<SIMConnectInfo> selectSIMConnectInfoListByAccessIp(@Param("accessIp") String accessIp);
    List<SIMConnectInfo> selectSIMConnectInfoListByServiceIdAndAccessIp(@Param("serviceId") String serviceId, @Param("accessIp") String accessIp);
}
