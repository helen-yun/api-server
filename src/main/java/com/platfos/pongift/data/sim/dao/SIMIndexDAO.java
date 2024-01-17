package com.platfos.pongift.data.sim.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Mapper
@Repository
public interface SIMIndexDAO {
    HashMap<String, String> callIndexReg01Sp(HashMap<String, String> paramMap);
    HashMap<String, String> callGiftCardReg01Sp(HashMap<String, String> paramMap);
}
