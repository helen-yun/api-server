package com.platfos.pongift.data.aim.dao;

import com.platfos.pongift.data.aim.model.AIMMessageInfo;
import com.platfos.pongift.data.sim.model.SIMConnectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AIMMessageInfoDAO {
    @Cacheable(value="AIMMessageInfoListCache")
    List<AIMMessageInfo> selectAIMMessageInfoList();
    List<AIMMessageInfo> selectAIMMessageInfoListByScopeGb(@Param("scope_gb") String scopeGb);
    List<AIMMessageInfo> selectAIMMessageInfoListByApplyKey(@Param("apply_key") String applyKey);
    List<String> selectApplyKeyList();
    List<String> selectLanguageGbList();
}
