package com.platfos.pongift.data.msm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MSMOperateInfo {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String operateId;
    private String storeId;
    private String dayGb;
    private String timeTp;
    private String openingTm;
    private String endingTm;
    private Date regDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String regId;
    private Date modDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String modId;
}