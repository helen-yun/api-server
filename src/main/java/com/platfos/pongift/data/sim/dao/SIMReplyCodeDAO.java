package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMReplyCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SIMReplyCodeDAO {
    List<SIMReplyCode> selectSIMReplyCodeList(@Param("api_gb") String apiGb);
    SIMReplyCode selectSIMReplyCodeByApplyKey(@Param("api_gb") String apiGb, @Param("apply_key") String applyKey);
    List<String> selectApplyKeyList(@Param("api_gb") String apiGb);
}
