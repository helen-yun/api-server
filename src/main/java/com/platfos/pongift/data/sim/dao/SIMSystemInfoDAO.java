package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMSystemInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SIMSystemInfoDAO {
    @Cacheable(value="SIMSystemInfoCache")
    List<SIMSystemInfo> selectSIMSystemInfoList();
    boolean existGateway(@Param("accessFl") String accessFl, @Param("channelGb") String channelGb, @Param("gatewayUrl") String gatewayUrl, @Param("methodTp") String methodTp);
}
