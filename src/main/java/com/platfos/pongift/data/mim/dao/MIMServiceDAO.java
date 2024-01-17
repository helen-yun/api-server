package com.platfos.pongift.data.mim.dao;

import com.platfos.pongift.data.mim.model.MIMService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MIMServiceDAO {
    MIMService selectMIMService(@Param("serviceId") String serviceId);
    MIMService selectMIMServiceListByChannelGb(@Param("channelGb") String channelGb);
    List<MIMService> selectMIMServiceListByAgencyId(@Param("agencyId") String agencyId);
    String selectChannelGbByServiceId(@Param("serviceId") String serviceId);
    long insertMIMService(MIMService service);
    int updateMIMService(MIMService service);
    int updateMIMServiceSt(@Param("serviceId") String serviceId, @Param("serviceSt") String serviceSt);
}
