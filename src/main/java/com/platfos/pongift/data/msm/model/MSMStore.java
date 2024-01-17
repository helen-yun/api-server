package com.platfos.pongift.data.msm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MSMStore {
    @JsonIgnore
    public static String OPTION_SEPARATOR = ",";

    private String storeId;
    private String serviceId;
    private long marketplaceStoreSeq;
    private String merchantNm;
    private String serviceNm;
    private String storeNm;
    private String zipCd;
    private String storeAddress1;
    private String storeAddress2;
    private String storeTel;
    private String reportTp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String offeringService;
    private String searchWay;
    private String approvalSt;
    private String approvalDt;
    private String useFl;
    private Date regDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String regId;
    private Date modDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String modId;

    private String storeImage1;
    private String storeImage2;
    private String storeImage3;
    private List<String> offeringServices;

    public void setOfferingServices(List<String> offeringServices){
        this.offeringServices = offeringServices;
        this.offeringService = null;
        if(offeringServices != null){
            this.offeringService = "";
            for(int i=0;i<offeringServices.size();i++){
                String option = offeringServices.get(i);
                this.offeringService += option + ((i<offeringServices.size()-1)?OPTION_SEPARATOR:"");
            }
        }
    }
}