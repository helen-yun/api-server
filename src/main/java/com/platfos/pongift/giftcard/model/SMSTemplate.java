package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.PhoneNumber;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMSTemplate {
    @Require
    @SimIndex(simIndex = SIMIndex.SIM_TEMPLATE_INFO)
    @Description("템플릿ID")
    private String templateId;

    @ParamGroup(groups = GroupA.class)
    @Require(groups = GroupA.class)
    @PhoneNumber
    @Description("수신자 휴대폰번호")
    private String phoneNo;

    @ParamGroup(groups = GroupA.class)
    @Description("dispatch_id")
    private String dispatchId;

    @Require
    @Description("템플릿 정보(공통>SMS 템플릿 코드 참조)")
    private Map<String, String> smsTemplateMap;
}
