package com.platfos.pongift.data.sim.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMTemplateInfo {
    private String templateId;
    private String formTp;
    private String codeTp;
    private String certifyTp;
    private String htmlCd;
    private String textCd;
    private Integer groundWidth;
    private Integer groundHeight;
    private Integer codeWidth;
    private Integer codeHeight;
    private String useSt;

    /** 알림톡 템플릿 코드 **/
    private String talkTemplateCd;
    /** 알림톡 템플릿 내용 **/
    private String talkContents;
    /** 알림톡 사용 여부 (N:미사용, Y:사용) **/
    private String talkUseSt;
    /** 알림톡 부가 정보 (버튼 및 첨부 항목) **/
    private String talkAddition;

    private Date regDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String regId;
    private Date modDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String modId;

    @Override
    public String toString() {
        return "SIMTemplateInfo{" +
                "templateId='" + templateId + '\'' +
                ", formTp='" + formTp + '\'' +
                ", codeTp='" + codeTp + '\'' +
                ", certifyTp='" + certifyTp + '\'' +
                ", htmlCd='" + htmlCd + '\'' +
                ", textCd='" + textCd + '\'' +
                ", groundWidth=" + groundWidth +
                ", groundHeight=" + groundHeight +
                ", codeWidth=" + codeWidth +
                ", codeHeight=" + codeHeight +
                ", useSt='" + useSt + '\'' +
                ", talkTemplateCd='" + talkTemplateCd + '\'' +
                ", talkContents='" + talkContents + '\'' +
                ", talkUseSt='" + talkUseSt + '\'' +
                ", talkAddition='" + talkAddition + '\'' +
                ", regDate=" + regDate +
                ", regId='" + regId + '\'' +
                ", modDate=" + modDate +
                ", modId='" + modId + '\'' +
                '}';
    }
}
