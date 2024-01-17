package com.platfos.pongift.data.sim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMAttachFile {
    private String attachId;
    private String maintainGb;
    private String linkedId;
    private String fileGb;
    private String filePath;
    private String storeNm;
    private String originNm;
    private String fileKind;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;
}
