package com.platfos.pongift.attach.model;

import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.File;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;

import java.util.List;

/**
 * Request Parameter 파일 객체(application/json 전용)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachUploadFile {
    @SimIndex(simIndex = SIMIndex.MIM_SERVICE)
    @Description("서비스ID")
    private String serviceId;

    @ParamGroup(groups = { GroupA.class })
    @Require(groups = { GroupA.class })
    @File(groups = { GroupA.class }, supportAttachType = { AttachDataType.URL, AttachDataType.BASE64 }, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("파일")
    private String data;

    @ParamGroup(groups = { GroupD.class })
    @Description("원본 파일명")
    private String fileName;

    @ParamGroup(groups = { GroupB.class })
    @Require(groups = { GroupB.class })
    @File(groups = { GroupB.class }, supportAttachType = { AttachDataType.URL, AttachDataType.BASE64 }, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("파일 리스트")
    private List<String> datas;
}
