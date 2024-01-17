package com.platfos.pongift.data.sim.service;

import com.platfos.pongift.data.sim.dao.SIMAttachFileDAO;
import com.platfos.pongift.definition.SIMAttach;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.data.sim.model.SIMAttachFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SIMAttachFileService {
    @Autowired
    SIMIndexService simIndexService;

    @Autowired
    SIMAttachFileDAO dao;

    public List<SIMAttachFile> getSIMAttachFileList(String linkedId) {
        return dao.selectSIMAttachFileListByLinkedId(linkedId);
    }
    public SIMAttachFile getSIMAttachFile(SIMAttach simAttach, String linkedId) {
        return dao.selectSIMAttachFileByMaintainGbAndFileGbAndLinkedId(simAttach.getMaintainGb(), simAttach.getFileGb(), linkedId);
    }
    public SIMAttachFile getSIMAttachFile(String maintainGb, String fileGb, String linkedId) {
        return dao.selectSIMAttachFileByMaintainGbAndFileGbAndLinkedId(maintainGb, fileGb, linkedId);
    }
    public String insertSIMAttachFile(SIMAttachFile attachFile) throws Exception {
        String attachId = simIndexService.getIndex(SIMIndex.SIM_ATTACH_FILE);
        attachFile.setAttachId(attachId);
        long rs = dao.insertSIMAttachFile(attachFile);

        if(rs>0) return attachId;
        else return null;
    }
    public int updateSIMAttachFile(SIMAttachFile attachFile) {
        return dao.updateSIMAttachFile(attachFile);
    }
    public int deleteSIMAttachFile(String attachId) {
        return dao.deleteSIMAttachFile(attachId);
    }
}
