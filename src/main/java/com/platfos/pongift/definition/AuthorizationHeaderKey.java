package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API Request Header Key
 */
@Getter
@AllArgsConstructor
public enum AuthorizationHeaderKey {
    SERVICE_ID_KEY("serviceId"),
    STORE_ID_KEY("storeId");

    private String key;
}
