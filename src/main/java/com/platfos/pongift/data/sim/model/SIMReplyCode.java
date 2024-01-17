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
public class SIMReplyCode {
    @JsonIgnore
    private String replyId;
    @JsonIgnore
    private String apiGb;

    private String replyCd;
    private String replyNm;

    @JsonIgnore
    private String codeSt;
    @JsonIgnore
    private String applyKey;
    @JsonIgnore
    private Date regDate;
    @JsonIgnore
    private String regId;
    @JsonIgnore
    private Date modDate;
    @JsonIgnore
    private String modId;

    @JsonGetter("code")
    public String getReplyCd(){
        return replyCd;
    }
    @JsonGetter("name")
    public String getReplyNm(){
        return replyNm;
    }
}
