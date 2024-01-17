package com.platfos.pongift.data.sim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SIMConnectInfo {
    /** 서비스접속ID **/
    private String connectId;
    /** 서비스ID **/
    private String serviceId;
    /** 매장ID **/
    private String storeId;
    /** 접속 서비스 유형 (01:운영, 02:개발) **/
    private String accessFl;
    /** 접속IP (예:127.0.0.1) **/
    private String accessIp;
    /** 등록 일시 **/
    private Date regDate;
    /** 등록자 **/
    private String regId;
    /** 변경 일시 **/
    private Date modDate;
    /** 변경자 **/
    private String modId;

    /** 서비스 타입 **/
    private String serviceTp;
    /** 서비스 상태 **/
    private String serviceSt;
    /**
     * 추가 권한
     */
    private String subServiceTp;
}
