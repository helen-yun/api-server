package com.platfos.pongift.definition;

import lombok.Getter;

@Getter
public enum TradeSeqTp {
    ORDER_NO("01", "거래번호"),
    APPROVAL_NO("02", "승인번호");

    private final String code;
    private final String name;

    TradeSeqTp(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
