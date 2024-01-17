package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMReplyCodeDAO;
import com.platfos.pongift.data.sim.dao.SIMTemplateInfoDAO;
import com.platfos.pongift.data.sim.model.SIMReplyCode;
import com.platfos.pongift.data.sim.model.SIMTemplateInfo;
import com.platfos.pongift.definition.FormTp;
import com.platfos.pongift.definition.SIMReplyCodeAPIGB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SIMTemplateInfoService {
    @Autowired
    SIMTemplateInfoDAO dao;

    public SIMTemplateInfo getSIMTemplateInfo(String templateId){
        return dao.selectSIMTemplateInfo(templateId);
    }
    public SIMTemplateInfo getSIMTemplateInfoByFormTp(FormTp formTp){
        return dao.selectSIMTemplateInfoByFormTp(formTp.getCode());
    }
}
