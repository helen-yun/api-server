package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.data.ssm.model.SSMSendInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GiftCardSendDirectInfo {
    /**
     * <p>
     * 알림톡 사용 여부
     * </p>
     */
    private boolean sendTalkSt;
    /**
     * <p>
     * 수신자 정보
     * </p>
     */
    private List<SSMSendInfo> sendInfoList;
}
