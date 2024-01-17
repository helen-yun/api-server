package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface SIMCodeDAO {
    SIMCode selectSIMCodeByCodeId(@Param("codeId") String codeId);
    SIMCode selectSIMCodeByGroupIdAndCodeCd(@Param("groupId") String groupId, @Param("codeCd") String codeCd);
    SIMCode selectSIMCodeByGroupIdAndCodeNm(@Param("groupId") String groupId, @Param("codeNm") String codeNm);
    @Cacheable(value="SIMCodeListCache")
    List<SIMCode> selectSIMCodeList();
    List<SIMCode> selectSIMCodeListByGroupId(@Param("groupId") String groupId);
    List<SIMCode> selectSIMCodeListByGroupIdAndCodeCds(@Param("groupId") String groupId, @Param("codeCds") List<String> codeCds);
    SIMCode selectSIMCodeListByGoodsCategoryCd(@Param("goodsCategoryCd") String goodsCategoryCd);

}
