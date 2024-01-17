package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMPolicyInfoDAO;
import com.platfos.pongift.data.sim.model.SIMPolicyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SIMPolicyInfoService {
    @Autowired
    SIMPolicyInfoDAO dao;

    public SIMPolicyInfo getSIMPolicyInfo(){
        return dao.selectSIMPolicyInfo();
    }
    public SIMPolicyInfo getDateLessThan(String column, String value, String format, String unit){
        return dao.selectDateLessThan(column, value, format, unit);
    }
    public SIMPolicyInfo selectDateLessThanOrEqual(String column, String value, String format, String unit){
        return dao.selectDateLessThanOrEqual(column, value, format, unit);
    }
}
