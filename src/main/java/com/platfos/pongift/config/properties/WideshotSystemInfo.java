package com.platfos.pongift.config.properties;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WideshotSystemInfo {
    private String host;
    private String key;
    private String callback;
    /** 알림톡 플러스 친구 채널명 **/
    private String plusFriendId;
    /** 알림톡 발신 프로필 키 **/
    private String senderKey;
}
