package com.platfos.pongift.data.aim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIMMessageInfo {
    private String languageId;
    private String scopeGb;
    private String applyKey;
    private String languageGb;
    private String messageNm;
    private Date regDate;
    private String regId;
    private Date modDate;
    private String modId;
}
