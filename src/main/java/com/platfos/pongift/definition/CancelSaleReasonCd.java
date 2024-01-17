package com.platfos.pongift.definition;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CancelSaleReasonCd {
    SOLD_OUT("01"),
    CS_ADMIN("02"),
    ETC("99"),
    ;
    
    private final String code;
    
    CancelSaleReasonCd(String code) {
        this.code = code;
    }
    
    public static CancelSaleReasonCd findByCode(String code) {
        if (code == null) return null;
    
        return Arrays.stream(values())
                       .filter(value -> value.name().equals(code))
                       .findFirst()
                       .orElse(null);
    }
}
