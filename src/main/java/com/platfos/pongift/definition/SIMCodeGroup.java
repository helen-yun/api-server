package com.platfos.pongift.definition;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 코드 그룹 정보
 */
@Getter
public enum SIMCodeGroup {
    businessTp("GF200301000001", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    paymentGb("GF200301000135", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    jubileeTp("GF200301000067", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    genderGb("GF200301000080", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    giftcardTp("GF200301000026", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    goodsOption("GF200301000051", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    optionCnt("GF200301000052"),
    taxFl("GF200301000053", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    //vatFl("GF200301000054", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    goodsApprovalSt("GF200301000055", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    guideTp("GF200301000140"),
    goodsProcessSt("GF200301000116"),
    exhibitSt("GF200301000141", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    //goodsCategory("GF200301000114", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    orderCd("GF200301000139", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    priceTp("GF200301000056", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    nationCd("GF200301000008", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    businessGb("GF200301000006"),
    countryGb("GF200301000007", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    serviceTp("GF200301000009"),
    serviceGb("GF200301000010", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    serviceOfflineCategory("GF200301000011", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, serviceGb, "01"),
    serviceOnlineCategory("GF200301000012", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, serviceGb, "02"),
    serviceMobileCategory("GF200301000013", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, serviceGb, "03"),
    serviceForms("GF200301000014", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    serviceFormsFranchise("GF200301000015", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, serviceForms, "01"),
    serviceFormsLocal("GF200301000016", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, serviceForms, "02"),
    appStore("GF200301000018", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    agencyFl("GF200301000019"),
    serviceSt("GF200301000020"),
    taxTp("GF200301000021"),
    salesGb("GF200301000022"),
    channelGb("GF200301000057", new APIRole[]{ APIRole.EXTERNAL_SERVICE, APIRole.PRODUCT_SUPPLY_AGENCY }),
    mimeType("GF200301000107"),
    reportTp("GF200301000062", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    offeringServices("GF200301000063", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    storeApprovalSt("GF200301000064"),
    tradeTp("GF200301000066", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    useFl("GF200301000065"),
    codeTp("GF200301000108"),
    certifyTp("GF200301000109"),
    useSt("GF200301000109"),
    expiryGb("GF200513000007", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    codeSt("GF200301000099"),
    dayGb("GF200301000058", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    timeTp("GF200301000124", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }),
    openingTm("GF200301000059", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, timeTp, "01", true),
    endingTm("GF200301000060", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY }, timeTp, "01", true),
    giftSt("GF200301000070", new APIRole[]{ APIRole.PRODUCT_SUPPLY_AGENCY, APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    wideShotRequest("GF200619000030", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL }),
    wideShotResult("GF200619000031", new APIRole[]{ APIRole.EXTERNAL_GATEWAY_CHANNEL });

    /** 기초코드 아이디 **/
    private String code;
    /** 부모 코드 그룹(의존관계) **/
    private SIMCodeGroup parentCodeGroup;
    /** 부모 코드 값(의존관계) **/
    private String parentValue;
    /** 부모 코드 값 의존 여부(API Document 전용) **/
    private boolean isNotUseParentURL;
    /** 접근 가능 권한 리스트 **/
    private List<String> roles;

    SIMCodeGroup(String code){
        this.code = code;
        this.roles = new ArrayList<>();
        this.isNotUseParentURL = false;
    }

    SIMCodeGroup(String code, APIRole[] roles){
        this.code = code;
        this.roles = new ArrayList<>();
        for(APIRole role : roles){
            this.roles.add(role.getAlias());
        }
        this.isNotUseParentURL = false;
    }

    SIMCodeGroup(String code, SIMCodeGroup parentCodeGroup, String parentValue){
        this.code = code;
        this.roles = new ArrayList<>();
        this.parentCodeGroup = parentCodeGroup;
        this.parentValue = parentValue;
        this.isNotUseParentURL = false;
    }

    SIMCodeGroup(String code, APIRole[] roles, SIMCodeGroup parentCodeGroup, String parentValue){
        this.code = code;
        this.roles = new ArrayList<>();
        for(APIRole role : roles){
            this.roles.add(role.getAlias());
        }
        this.parentCodeGroup = parentCodeGroup;
        this.parentValue = parentValue;
        this.isNotUseParentURL = false;
    }

    SIMCodeGroup(String code, APIRole[] roles, SIMCodeGroup parentCodeGroup, String parentValue, boolean isNotUseParentURL){
        this.code = code;
        this.roles = new ArrayList<>();
        for(APIRole role : roles){
            this.roles.add(role.getAlias());
        }
        this.parentCodeGroup = parentCodeGroup;
        this.parentValue = parentValue;
        this.isNotUseParentURL = isNotUseParentURL;
    }

    public static SIMCodeGroup findByCode(String code){
        if(code == null) return null;

        for(SIMCodeGroup codeGroup : values()){
            if(code.equals(codeGroup.getCode())){
                return codeGroup;
            }
        }
        return null;
    }

    public static SIMCodeGroup findByParentCodeGroup(SIMCodeGroup parentCodeGroup, String parentValue){
        if(parentCodeGroup == null) return null;
        if(parentValue == null) return null;

        for(SIMCodeGroup codeGroup : values()){
            if(codeGroup.getParentCodeGroup() != null && codeGroup.getParentValue() != null){
                if(codeGroup.getParentCodeGroup() == parentCodeGroup & codeGroup.getParentValue().equals(parentValue)){
                    return codeGroup;
                }
            }
        }
        return null;
    }

    public static SIMCodeGroup[] findChildCodeGroupByParentCodeGroup(SIMCodeGroup parentCodeGroup){
        if(parentCodeGroup == null) return null;

        List<SIMCodeGroup> children = new ArrayList<>();
        for(SIMCodeGroup codeGroup : values()){
            if(codeGroup.getParentCodeGroup() != null && codeGroup.getParentValue() != null){
                if(codeGroup.getParentCodeGroup() == parentCodeGroup){
                    children.add(codeGroup);
                }
            }
        }
        return children.toArray(new SIMCodeGroup[children.size()]);
    }
}
