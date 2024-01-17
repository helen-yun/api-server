package com.platfos.pongift.store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.msm.model.MSMOperateInfo;
import com.platfos.pongift.data.msm.model.MSMStore;
import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.*;
import com.platfos.pongift.validate.constraints.Number;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import com.platfos.pongift.validate.group.GroupC;
import com.platfos.pongift.validate.group.GroupD;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
@Conditional(selected = "serviceGb", targets = "categoryTp")
@Conditional(selected = "serviceForm", targets = "serviceForm2")
@Conditional(requiredGroup = {"applicationNm","appStore"})
@Conditional(requiredGroup = {"zipCd","storeAddress1","storeAddress2"})
public class Store {
    /** MIMService **/
    @ParamGroup(groups = {GroupD.class})
    @SimIndex(simIndex = SIMIndex.MIM_SERVICE)
    @Description("서비스ID")
    private String serviceId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimIndex(simIndex = SIMIndex.MIM_MERCHANT)
    @Description("사업자ID")
    private String merchantId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.businessGb)
    @Description("사업 구분")
    private String businessGb;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.countryGb)
    @Description("국내/외 구분")
    private String countryGb;

    @SimCode(groupCode = SIMCodeGroup.nationCd)
    @Description("국가명/국가코드")
    private String nationCd;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Description("서비스 명")
    @Length(max = 100)
    private String serviceNm;

    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.serviceTp)
    @Description("서비스 유형")
    private String serviceTp;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.serviceGb)
    @Description("서비스 구분")
    private String serviceGb;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @SimCode
    @Description("서비스 카테고리")
    private String categoryTp;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.serviceForms)
    @Description("서비스 형태")
    private String serviceForm;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @SimCode
    @Description("서비스 세부 형태")
    private String serviceForm2;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @CustomCode(codes = {"Y", "N"})
    @Description("정산 유무")
    private String adjustFl;

    @Length(max = 200)
    @Description("서비스 URL")
    private String serviceUrl;

    @Length(max = 50)
    @Description("애플리케이션 명")
    private String applicationNm;

    @SimCode(groupCode = SIMCodeGroup.appStore)
    @Description("지원 앱스토어")
    private String appStore;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.agencyFl)
    @Description("대행사 유무")
    private String agencyFl;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimIndex(simIndex = SIMIndex.MIM_SERVICE, groups = {GroupD.class})
    private String agencyId;

    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.serviceSt)
    @Description("서비스 상태")
    private String serviceSt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.taxTp)
    @Description("세금계산서 발행 유형")
    private String taxTp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.salesGb)
    @Description("매출 구분")
    private String salesGb;

    @Number
    @Length(max = 20)
    @Description("고객상담 연락처")
    private String centerTel;

    @Length(max = 20)
    @Description("고객센터 영업시간")
    private String centerTm;

    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("프렌차이즈 본사 매장ID")
    private String ownedId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @CustomCode(codes = {"01", "02"})
    @Description("저장 상태")
    private String saveSt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @DateTime(format = "yyyyMMdd")
    @Description("적용일")
    private String applyDt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Length(max = 20)
    @Description("본사 제휴 담당자")
    private String alignerId;

    /** MSMStore **/
    @ParamGroup(groups = {GroupB.class, GroupC.class})
    @Require(groups = {GroupB.class, GroupC.class})
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("사업자 명")
    private String merchantNm;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @Length(max = 100)
    @Description("매장명")
    private String storeNm;

    @Length(max = 10)
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @Description("우편번호")
    private String zipCd;

    @Length(max = 200)
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @Description("매장 주소")
    private String storeAddress1;

    @Length(max = 100)
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @Description("매장 상세 주소")
    private String storeAddress2;

    @Number
    @Length(max = 20)
    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @Description("매장 연락처")
    private String storeTel;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.reportTp)
    @Description("웹사이트/SNS")
    private String reportTp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("제공 서비스")
    private String offeringService;

    @Length(max = 500)
    @Description("찾아가는 길")
    private String searchWay;

    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.storeApprovalSt)
    @Description("승인 상태")
    private String approvalSt;

    @ParamGroup(groups = {GroupD.class})
    @DateTime(format = "yyyyMMdd")
    @Description("승인 상태 변경 일자")
    private String approvalDt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @SimCode(groupCode = SIMCodeGroup.useFl)
    @Description("활성화 여부")
    private String useFl;

    @ParamGroup(groups = {GroupD.class})
    @Description("등록 일시")
    private Date regDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("등록자")
    private String regId;

    @ParamGroup(groups = {GroupD.class})
    @Description("변경 일시")
    private Date modDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ParamGroup(groups = {GroupD.class})
    @Description("변경자")
    private String modId;

    @File(supportAttachType = { AttachDataType.URL, AttachDataType.BASE64}, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("매장 이미지 1")
    private String storeImage1;

    @File(supportAttachType = { AttachDataType.URL, AttachDataType.BASE64}, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("매장 이미지 2")
    private String storeImage2;

    @File(supportAttachType = { AttachDataType.URL, AttachDataType.BASE64}, supportFileFormat = {"jpeg", "jpg", "bmp", "png", "gif"})
    @Description("매장 이미지 3")
    private String storeImage3;

    @ParamGroup(groups = {GroupA.class, GroupB.class})
    @Require(groups = {GroupA.class, GroupB.class})
    @SimCode(groupCode = SIMCodeGroup.offeringServices)
    @Description("제공 서비스")
    private List<String> offeringServices;

    @Valid
    @Description("매장 운영시간 정보")
    private List<StoreOperate> operates;

    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("제휴몰 매장 pk")
    private long marketplaceStoreSeq;

    @JsonIgnore
    public MIMService mimService(){
        return MIMService.builder()
                .serviceId(getServiceId())
                .merchantId(getMerchantId())
                .businessGb(getBusinessGb())
                .countryGb(getCountryGb())
                .nationCd(getNationCd())
                .serviceNm(getServiceNm())
                .serviceTp(getServiceTp())
                .serviceGb(getServiceGb())
                .categoryTp(getCategoryTp())
                .serviceForm(getServiceForm())
                .serviceForm2(getServiceForm2())
                .adjustFl(getAdjustFl())
                .serviceUrl(getServiceUrl())
                .applicationNm(getApplicationNm())
                .appStore(getAppStore())
                .agencyFl(getAgencyFl())
                .agencyId(getAgencyId())
                .serviceSt(getServiceSt())
                .taxTp(getTaxTp())
                .salesGb(getSalesGb())
                .centerTel(getCenterTel())
                .centerTm(getCenterTm())
                .ownedId(getOwnedId())
                .saveSt(getSaveSt())
                .applyDt(getApplyDt())
                .build();
    }

    @JsonIgnore
    public MSMStore msmStore(){
        MSMStore msmStore = MSMStore.builder()
                .storeId(getStoreId())
                .serviceId(getServiceId())
                .merchantNm(getMerchantNm())
                .serviceNm(getServiceNm())
                .storeNm(getStoreNm())
                .zipCd(getZipCd())
                .storeAddress1(getStoreAddress1())
                .storeAddress2(getStoreAddress2())
                .storeTel(getStoreTel())
                .reportTp(getReportTp())
                .searchWay(getSearchWay())
                .approvalSt(getApprovalSt())
                .approvalDt(getApprovalDt())
                .useFl(getUseFl())
                .storeImage1(getStoreImage1())
                .storeImage2(getStoreImage2())
                .storeImage3(getStoreImage3())
                .marketplaceStoreSeq(getMarketplaceStoreSeq())
                .build();

        msmStore.setOfferingServices(getOfferingServices());

        return msmStore;
    }

    @JsonIgnore
    public List<MSMOperateInfo> msmOperateInfos(){
        List<MSMOperateInfo> msmOperateInfos = new ArrayList<>();
        if(!ObjectUtils.isEmpty(operates)){
            for(StoreOperate operate : operates){
                MSMOperateInfo msmOperateInfo = MSMOperateInfo.builder()
                        .storeId(storeId)
                        .dayGb(operate.getDayGb())
                        .timeTp(operate.getTimeTp())
                        .openingTm(operate.getOpeningTm())
                        .endingTm(operate.getEndingTm())
                        .build();

                if(operate.getTimeTp().equals("02")){
                    if(StringUtils.isEmpty(msmOperateInfo.getOpeningTm())) msmOperateInfo.setOpeningTm("01");
                    if(StringUtils.isEmpty(msmOperateInfo.getEndingTm())) msmOperateInfo.setEndingTm("01");
                    //msmOperateInfo.setOpeningTm(null);
                    //msmOperateInfo.setEndingTm(null);
                }
                msmOperateInfos.add(msmOperateInfo);
            }
        }
        return msmOperateInfos;
    }
}
