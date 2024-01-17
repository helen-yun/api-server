package com.platfos.pongift.data.sim.dao;

import com.platfos.pongift.data.sim.model.SIMAttachFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SIMAttachFileDAO {
    List<SIMAttachFile> selectSIMAttachFileListByLinkedId(@Param("linkedId") String linkedId);
    SIMAttachFile selectSIMAttachFileByMaintainGbAndFileGbAndLinkedId(@Param("maintainGb") String maintainGb, @Param("fileGb") String fileGb, @Param("linkedId") String linkedId);
    long insertSIMAttachFile(SIMAttachFile simAttachFile);
    int updateSIMAttachFile(SIMAttachFile simAttachFile);
    int deleteSIMAttachFile(@Param("attachId") String attachId);
}
