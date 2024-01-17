package com.platfos.pongift.data.sim.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMSystemInfo {
    private String systemId;
    private String accessFl;
    private String applyKey;
    private String systemNm;

    @JsonIgnore
    private Date regDate;
    @JsonIgnore
    private String regId;
    @JsonIgnore
    private Date modDate;
    @JsonIgnore
    private String modId;
}
