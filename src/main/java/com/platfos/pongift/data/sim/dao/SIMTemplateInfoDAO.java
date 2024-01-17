package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMTemplateInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SIMTemplateInfoDAO {
    SIMTemplateInfo selectSIMTemplateInfo(@Param("templateId") String templateId);
    SIMTemplateInfo selectSIMTemplateInfoByFormTp(@Param("formTp") String formTp);
}
