package com.platfos.pongift.store.service;

import com.platfos.pongift.authorization.exception.APIPermissionAuthExceptionForNoLogger;
import com.platfos.pongift.authorization.service.APIPermissionAuthService;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.gim.service.GIMGoodsService;
import com.platfos.pongift.data.msm.model.MSMOperateInfo;
import com.platfos.pongift.data.msm.service.MSMOperateInfoService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.msm.model.MSMStore;
import com.platfos.pongift.data.msm.service.MSMStoreService;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.giftcard.exeption.GiftException;
import com.platfos.pongift.store.dao.StoreDAO;
import com.platfos.pongift.store.exception.ProductsOnDisplayException;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.store.model.StoreOperate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {
    private final ApplicationProperties properties;
    private final SIMCodeService simCodeService;
    private final MIMServiceService mimServiceService;
    private final MSMStoreService msmStoreService;
    private final GIMGoodsService gimGoodsService;
    private final StoreDAO storeDAO;
    private final MSMOperateInfoService msmOperateService;
    private final APIPermissionAuthService permissionAuthService;

    public List<Store> getStoreList(String agencyId) {
        List<Store> list = storeDAO.selectStoreListByAgencyId(agencyId, properties.getSystemMode());
        for (Store store : list) setStoreInfo(store);
        return list;
    }

    public List<Store> getStoreList(String agencyId, String ownedId) {
        List<Store> list = storeDAO.selectStoreListByAgencyIdAndOwnedId(agencyId, ownedId, properties.getSystemMode());
        for (Store store : list) setStoreInfo(store);
        return list;
    }

    public List<Store> getOwnerStoreList(String agencyId) {
        List<Store> list = storeDAO.selectOwnerStoreListByAgencyId(agencyId, properties.getSystemMode());
        for (Store store : list) setStoreInfo(store);
        return list;
    }

    public Store getStore(String storeId) {
        return getStore(null, storeId);
    }

    public Store getStore(String agencyId, String storeId) {
        Store store = storeDAO.selectStore(agencyId, storeId, properties.getSystemMode());
        setStoreInfo(store);
        return store;
    }

    public Store getStoreByGoodsId(String goodsId) {
        return storeDAO.selectStoreByGoodsId(goodsId);
    }

    /**
     * request 권한 별 매장 정보 조회
     *
     * @param request
     * @param storeId
     * @return
     * @throws GiftException
     */
    public Store getStoreByPermission(HttpServletRequest request, String storeId) throws GiftException {
        Store store;
        if (permissionAuthService.hasRole(request, APIRole.PRODUCT_SUPPLY_AGENCY)) { //상품 공급 대행사
            String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey()); //대행사 아이디
            store = getStore(agencyId, storeId); //매장 정보 조회
            if (ObjectUtils.isEmpty(store))
                throw new APIPermissionAuthExceptionForNoLogger("No API Permission Authorization : getStoreByPermission => agencyId : " + agencyId + ", storeId : " + storeId);
        } else { //기타 권한
            store = getStore(storeId); //매장 정보 조회
            if (ObjectUtils.isEmpty(store)) throw new GiftException(ResponseCode.FAIL_GIFT_SERVICE_ST_NOT_USED);
        }
        return store;
    }

    /**
     * 매장 서비스 상태 확인
     *
     * @param store
     * @throws GiftException
     */
    public void checkStoreSt(Store store) throws GiftException {
        if (store != null && MIMServiceSt.findByCode(store.getServiceSt()) != MIMServiceSt.ACTIVE) {
            throw new GiftException(ResponseCode.FAIL_GIFT_SERVICE_ST_NOT_USED);
        }
    }

    @Transactional
    public Store insertStore(Store store) throws Exception {
        String serviceId = mimServiceService.insertMIMService(store.mimService());
        if (!StringUtils.isEmpty(serviceId)) {
            store.setServiceId(serviceId);
            String storeId = msmStoreService.insertMSMStore(store.msmStore());
            if (!StringUtils.isEmpty(storeId)) store.setStoreId(storeId);
            else return null;

            msmOperateService.deleteMSMOperateInfo(store.getStoreId());
            msmOperateService.insertMSMOperateInfos(store.msmOperateInfos());
        } else {
            return null;
        }
        return store;
    }

    @Transactional
    public Store updateStore(Store store) throws Exception {
        int rsService = mimServiceService.updateMIMService(store.mimService());
        if (rsService > 0) {
            int rsStore = msmStoreService.updateMSMStore(store.msmStore());
            if (rsStore > 0) {
                msmOperateService.deleteMSMOperateInfo(store.getStoreId());
                msmOperateService.insertMSMOperateInfos(store.msmOperateInfos());
                return store;
            }
        }
        return null;
    }

    @Transactional
    public int updateMIMServiceSt(String agencyId, String serviceId, MIMServiceSt serviceSt) throws ProductsOnDisplayException {
        if (serviceSt == MIMServiceSt.TERMINATION) {
            if (gimGoodsService.existGIMGoodsByExhibitSt(agencyId, ExhibitSt.EXHIBITION_SALE)) {
                throw new ProductsOnDisplayException();
            }
        }
        return mimServiceService.updateMIMServiceSt(serviceId, serviceSt);
    }


    /**
     * POS 전용::대행사(상품공급)
     **/
    public void setCustomDefaultStoreByAgency(String agencyId, Store store) {
        /** MIMService 부분 **/
        store.setAgencyId(agencyId);

        MIMService mimAgencyService = mimServiceService.getMIMService(agencyId);
        store.setMerchantId(mimAgencyService.getMerchantId());

        if (!StringUtils.isEmpty(store.getStoreId())) {
            MSMStore msmStore = msmStoreService.getMSMStore(store.getStoreId());
            if (msmStore != null) {
                store.setServiceId(msmStore.getServiceId());
            }
        }
        store.setBusinessGb("01"); //사업 구분(01:PONGIFT, 02:추가)

        store.setServiceNm(store.getStoreNm());

        store.setServiceTp(APIRole.PRODUCT_SUPPLIER.getCode()); //서비스 유형 (01:상품권사업자, 02:상품공급사, 03:유통채널사, 04:대행사(상품공급), 05:결제(P/G)서비스, 06:메시징서비스, 07:B2B기업고객, 99:기타제휴)
        store.setAdjustFl("N"); //정산 유무 (N:미정산 Y:정산)
        store.setAgencyFl("Y"); //대행사 통한 store 관리 유무 (N:자체, Y:대행)
        store.setTaxTp("01"); //세금계산서 발행 유형 (01:청구발행, 02:영수발행, 03:영수수신, 04:인보이스발행, 05:인보이스수신)
        store.setSalesGb("03"); //매출 구분 (01:전체, 02:매출, 03:매입)
        store.setSaveSt("02"); //저장 상태 (01:임시저장, 02:최종저장)

        /** MSMStore 부분 **/
        store.setApprovalSt("02"); //매장 승인 상태 (01:미승인, 02:승인, 03:반려)
    }

    private Store setStoreInfo(Store store) {
        if (store != null) {
            store.setOfferingServices(getStoreOfferingServiceList(store.getOfferingService()));
            store.setOperates(getStoreOperates(store.getStoreId()));
        }
        return store;
    }

    private List<StoreOperate> getStoreOperates(String storeId) {
        List<StoreOperate> storeOperates = new ArrayList<>();
        List<MSMOperateInfo> msmOperateInfos = msmOperateService.getMSMOperateInfoList(storeId);
        for (MSMOperateInfo msmOperateInfo : msmOperateInfos) {
            storeOperates.add(StoreOperate.builder()
                    .dayGb(msmOperateInfo.getDayGb())
                    .timeTp(msmOperateInfo.getTimeTp())
                    .openingTm(msmOperateInfo.getOpeningTm())
                    .endingTm(msmOperateInfo.getEndingTm())
                    .regDate(msmOperateInfo.getRegDate())
                    .modDate(msmOperateInfo.getModDate())
                    .build());
        }
        return storeOperates;
    }

    private List<String> getStoreOfferingServiceList(String offeringSerivce) {
        List<String> options = new ArrayList<>();

        if (!StringUtils.isEmpty(offeringSerivce)) {
            List<SIMCode> dbOptions = simCodeService.getSIMCodeList(SIMCodeGroup.offeringServices);
            String[] arrOptions = offeringSerivce.split(MSMStore.OPTION_SEPARATOR);
            for (String option : arrOptions) {
                for (SIMCode dbOption : dbOptions) {
                    if (dbOption.getCodeCd().equals(option)) {
                        options.add(dbOption.getCodeCd());
                        break;
                    }
                }
            }
        }
        return options;
    }
}
