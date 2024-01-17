package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.cim.model.CIMCancelableUseLedger;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.model.CIMLedgerHis;
import com.platfos.pongift.data.sim.model.SIMPolicyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface SIMPolicyInfoDAO {
    SIMPolicyInfo selectSIMPolicyInfo();
    SIMPolicyInfo selectDateLessThan(@Param("column") String column, @Param("value") String value, @Param("format") String format, @Param("unit") String unit);
    SIMPolicyInfo selectDateLessThanOrEqual(@Param("column") String column, @Param("value") String value, @Param("format") String format, @Param("unit") String unit);
}
