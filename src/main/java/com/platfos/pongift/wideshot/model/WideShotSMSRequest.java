package com.platfos.pongift.wideshot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.*;
import com.platfos.pongift.validate.constraints.Number;
import com.platfos.pongift.validate.group.*;
import com.platfos.pongift.wideshot.definition.WideShot;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WideShotSMSRequest implements WideShotRequest {
    @Length(max = 12)
    @Number
    @Description("발신번호")
    private String callback;

    @ParamGroup(groups = { GroupA.class })
    @Require(groups = { GroupA.class })
    @Length(max = 80)
    @Description("내용")
    private String sms;

    @ParamGroup(groups = { GroupB.class, GroupE.class })
    @Require(groups = { GroupB.class, GroupE.class })
    @Length(max = 2000)
    @Description("내용")
    private String contents;

    @Require
    @Length(max = 12)
    @PhoneNumber
    @Description("수신자 전화번호")
    private String receiverTelNo;

    @Description("발송코드 (문자 고유 식별자)")
    private String userKey;

    @ParamGroup(groups = { GroupB.class, GroupC.class, GroupE.class })
    @Require(groups = { GroupB.class, GroupC.class, GroupE.class })
    @Length(max = 100)
    @Description("문자 제목")
    private String title;

    @ParamGroup(groups = { GroupA.class, GroupB.class, GroupC.class, GroupD.class })
    @CustomCode(codes = {"Y","N"})
    @Description("광고 여부 (기본:N)")
    private String advertisementYn;

    @ParamGroup(groups = {GroupC.class })
    @Require(groups = { GroupC.class })
    @Size(min = 1, max = 3)
    @File(maxFileSize = "1MB", maxImageWidth = 1000, maxImageHeight = 1000, supportAttachType = { AttachDataType.URL, AttachDataType.BASE64 }, supportFileFormat = {"jpg"})
    @Description("MMS 이미지 파일")
    private List<String> images;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class })
    private List<Map<String, Object>> imageFiles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupF.class })
    @Description("알림톡 템플릿 코드")
    private String templateCode;

    @ParamGroup(groups = { GroupE.class })
    @Require(groups = { GroupE.class })
    @Length(max = 4096)
    @Description("알림톡 부가 정보 (버튼 및 첨부 항목) - JSON")
    private String attachment;

    @Override
    public MultiValueMap<String, Object> getParams(Map<String, Object> info) {
        WideShot wideShotType = (WideShot) info.get("wideShotType");
        String defaultCallback = (String) info.get("callback");
        String defaultUserKey = (String) info.get("userKey");

        if(StringUtils.isEmpty(callback)) callback = defaultCallback;
        if(StringUtils.isEmpty(userKey)) userKey = defaultUserKey;

        MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
        map.add("callback", callback);

        if(wideShotType == WideShot.SMS){
            map.add("contents", sms);
        }else if(wideShotType == WideShot.LMS | wideShotType == WideShot.MMS | wideShotType == WideShot.ALIMTALK){
            map.add("contents", contents);
        }

        if(StringUtils.isNotEmpty(receiverTelNo)){
            receiverTelNo = receiverTelNo.replaceAll("-", "");
            receiverTelNo = receiverTelNo.replaceAll(" ", "");
        }

        map.add("receiverTelNo", receiverTelNo);
        map.add("userKey", userKey);
        if(wideShotType == WideShot.LMS | wideShotType == WideShot.MMS | wideShotType == WideShot.ALIMTALK) map.add("title", title);
        if(wideShotType != WideShot.ALIMTALK) map.add("advertisementYn", advertisementYn);

        if(wideShotType == WideShot.MMS){
            if(!ObjectUtils.isEmpty(imageFiles)){
                int max = imageFiles.size()>3?3:imageFiles.size();

                for(int i = 0; i < max; i++){
                    Map<String, Object> file = imageFiles.get(i);
                    if(file != null){
                        map.add("imageFile"+(i+1), new ByteArrayResource((byte[]) file.get("bytes")) {
                            @Override
                            public String getFilename() throws IllegalStateException {
                                return ((String) file.get("fileName"));
                            }
                        });
                    }
                }
            }
        }

        if(wideShotType == WideShot.ALIMTALK){
            map.add("plusFriendId", (String) info.get("plusFriendId"));
            map.add("senderKey", (String) info.get("senderKey"));
            map.add("templateCode", templateCode);
            map.add("attachment", attachment);
        }

        return map;
    }
}
