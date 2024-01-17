package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Request 기본 속성 키
 */
@Getter
@AllArgsConstructor
public enum AttributeKey {
    DATE_TIME_KEY("dateTime"),
    IP_ADDRESS_KEY("ipAddress"),
    PARAMS_KEY("params"),
    API_ROLES("apiRoles");

    private String key;
}
