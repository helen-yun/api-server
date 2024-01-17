package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 시스템 정보
 */
@Getter
@AllArgsConstructor
public enum SystemKey {
    CUSTOMER_CENTER_TEL("customer.center.tel"),
    CUSTOMER_CENTER_EMAIL("customer.center.email"),
    CUSTOMER_CENTER_ADDRESS("customer.center.address"),
    UPLOAD_SERVER_HOST("upload.server.host"),
    CDN_SERVER_HOST("cdn.server.host"),
    QUANTUM_SERVER_HOST("quantum.server.host"),
    GOODS_COMMON_IMAGE("goods.common.image"),
    ALARM_TELEGRAM_BOTID("alarm.telegram.bot-id"),
    ALARM_TELEGRAM_CHATID("alarm.telegram.chat-id");

    private String key;

    public static SystemKey findByKey(String key){
        if(key == null) return null;

        for(SystemKey systemKey : values()){
            if(key.equals(systemKey.getKey())){
                return systemKey;
            }
        }
        return null;
    }
}
