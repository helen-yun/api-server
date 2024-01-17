package com.platfos.pongift.data.mim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MIMService {
    private String serviceId;
    private String merchantId;
    private String businessGb;
    private String countryGb;
    private String nationCd;
    private String serviceNm;
    private String serviceTp;
    private String serviceGb;
    private String categoryTp;
    private String serviceForm;
    private String serviceForm2;
    private String adjustFl;
    private String serviceUrl;
    private String applicationNm;
    private String appStore;
    private String agencyFl;
    private String agencyId;
    private String serviceSt;
    private String taxTp;
    private String salesGb;
    private String centerTel;
    private String centerTm;
    private String ownedId;
    private String saveSt;
    private String applyDt;
    private String alignerId;
    private Date regDate;
    @JsonIgnore
    private String regId;
    private Date modDate;
    @JsonIgnore
    private String modId;
}
