package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.dao.SIMCodeDAO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SIMCodeService {
    @Autowired
    SIMCodeDAO dao;

    @CacheEvict(value = "SIMCodeListCache", allEntries = true)
    public void refreshSIMCodeListCache(){}

    public SIMCode getSIMCode(String codeId){
        if(StringUtils.isEmpty(codeId)) return null;
        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            if(simCode.getCodeId().equals(codeId)) return simCode;
        }
        return null;
    }
    public SIMCode getSIMCode(SIMCodeGroup groupCd, String codeCd){
        if(groupCd == null) return null;
        if(StringUtils.isEmpty(codeCd)) return null;

        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            if(simCode.getGroupId().equals(groupCd.getCode()) & simCode.getCodeCd().equals(codeCd)) return simCode;
        }
        return null;
    }

    public SIMCode getSIMCodeBuCodeNm(SIMCodeGroup groupCd, String codeNm){
        if(groupCd == null) return null;
        if(StringUtils.isEmpty(codeNm)) return null;

        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            if(simCode.getGroupId().equals(groupCd.getCode()) & simCode.getCodeNm().equals(codeNm)) return simCode;
        }
        return null;
    }
    public List<SIMCode> getSIMCodeList(){
        return dao.selectSIMCodeList();
    }
    public List<SIMCode> getSIMCodeList(SIMCodeGroup groupCd){
        List<SIMCode> codeList = null;
        if(groupCd == null) return null;

        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            if(simCode.getGroupId().equals(groupCd.getCode())){
                if(ObjectUtils.isEmpty(codeList)) codeList = new ArrayList<>();
                codeList.add(simCode);
            }
        }
        return codeList;
    }
    public List<SIMCode> getSIMCodeList(SIMCodeGroup groupCd, List<String> codeCds){
        List<SIMCode> codeList = null;
        if(groupCd == null) return null;

        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            for(String codeCd : codeCds){
                if(simCode.getGroupId().equals(groupCd.getCode()) & simCode.getCodeCd().equals(codeCd)) {
                    if(ObjectUtils.isEmpty(codeList)) codeList = new ArrayList<>();
                    codeList.add(simCode);
                }
            }
        }
        return codeList;
    }

    public SIMCode getSIMCodeByGoodsCategoryCd(String goodsCategoryCd){
        if(StringUtils.isEmpty(goodsCategoryCd)) return null;
        return dao.selectSIMCodeListByGoodsCategoryCd(goodsCategoryCd);
    }

    public String getSIMCodeGroupName(SIMCodeGroup groupCd){
        if(groupCd == null) return null;

        List<SIMCode> list = getSIMCodeList();
        for(SIMCode simCode : list){
            if(simCode.getGroupId().equals(groupCd.getCode())) {
                return simCode.getGroupNm();
            }
        }
        return null;
    }
}
