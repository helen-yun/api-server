package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 유효기간 날짜 구분
 * (01:93일, 02:1년, 03:5년)
 */
@Getter
@AllArgsConstructor
public enum ExpiryDay {
    DAYS_93("01", 93),
    ONE_YEAR("02", 365),
    THREE_YEAR("04", 1095),
    FIVE_YEAR("03", 1825);

    private String code;
    private int value;

    public static int findByCode(String code) {
        if (code == null) return 0;
        for (ExpiryDay expiryDay : values()) {
            if(expiryDay.getCode().equals(code)) {
                return expiryDay.getValue();
            }
        }
        return 0;
    }
}
