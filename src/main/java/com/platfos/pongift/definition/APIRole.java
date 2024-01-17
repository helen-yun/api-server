package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 권한
 */
@Getter
@AllArgsConstructor
public enum APIRole {
    GIFT_BUSINESS("01", "상품권사업자", "GIFT"),
    PRODUCT_SUPPLIER("02", "상품공급사", "PRODUCT"),
    DISTRIBUTION_CHANNEL("03", "유통채널사", "CHANNEL"),
    PRODUCT_SUPPLY_AGENCY("04", "대행사(상품공급)", "AGENCY"),
    PAYMENT_SERVICE("05", "결제(P/G) 서비스", "PAYMENT"),
    MESSAGING_SERVICE("06", "메시징 서비스", "MESSAGE"),
    B2B("07", "B2B 기업고객", "B2B"),
    EXTERNAL_SERVICE("99", "Platfos 연동 서비스", "PLATFOS"),
    EXTERNAL_GATEWAY_CHANNEL("98", "유통채널 GateWay", "GATEWAY-CHANNEL"),
    ANONYMOUS("00", "비인증", "ANONYMOUS"),
    AUTHENTICATED("11", "인증", "AUTHENTICATED"),
    TICKETING("08", "상품권 수기발송", "TICKETING");

    private String code;
    private String name;
    private String alias;


    public static APIRole findByCode(String code){
        if(code == null) return null;

        for(APIRole apiRole : values()){
            if(code.equals(apiRole.getCode())){
                return apiRole;
            }
        }
        return null;
    }

    public static APIRole findByAlias(String alias){
        if(alias == null) return null;

        for(APIRole apiRole : values()){
            if(alias.equals(apiRole.getAlias())){
                return apiRole;
            }
        }
        return null;
    }
}
