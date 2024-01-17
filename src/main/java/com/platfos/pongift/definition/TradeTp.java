package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.Alias;

/**
 * 상품권 거래 유형
 */
@Getter
@AllArgsConstructor
@Alias("POMTradeSeqNo")
public enum TradeTp {
    /**
     * 판매
     */
    SALES("01"),

    /**
     * 판매취소
     */
    SALES_CANCEL("02"),

    /**
     * 사용
     */
    USE("03"),

    /**
     * 사용취소
     */
    USE_CANCEL("04"),

    /**
     * 환불
     */
    REFUND("05"),

    /**
     * 선물하기
     */
    GIFT("06"),

    /**
     * 반품요청
     */
    RETURN_REQUESTED("07"),

    /**
     * 반품승인
     */
    RETURN("08"),

    /**
     * 직발송
     */
    SEND_DIRECT("09"),
    /**
     * 반품취소
     */
    RETURN_CANCEL("10"),
    /**
     * 반품거부
     */
    RETURN_REJECT("11"),
    ;

    private String code; // GF200301000066

    public static TradeTp findByCode(String code) {
        if (code == null) return null;

        for (TradeTp tradeTp : values()) {
            if (code.equals(tradeTp.getCode())) {
                return tradeTp;
            }
        }
        return null;
    }
}
