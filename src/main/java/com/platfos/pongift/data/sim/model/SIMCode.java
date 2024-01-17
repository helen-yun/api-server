package com.platfos.pongift.data.sim.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMCode {
    @JsonIgnore
    private String codeId;
    @JsonIgnore
    private String groupId;
    @JsonIgnore
    private String groupNm;

    @Description("코드")
    private String codeCd;
    @Description("코드명")
    private String codeNm;

    @JsonIgnore
    private String codeDesc;
    @JsonIgnore
    private String codeSt;

    @JsonIgnore
    private Date regDate;
    @JsonIgnore
    private String regId;
    @JsonIgnore
    private Date modDate;
    @JsonIgnore
    private String modId;

    @JsonGetter("code")
    public String getCodeCd(){
        return codeCd;
    }
    @JsonGetter("name")
    public String getCodeNm(){
        return codeNm;
    }

}
