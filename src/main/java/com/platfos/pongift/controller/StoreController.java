package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.authorization.exception.APIPermissionAuthExceptionForNoLogger;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.store.model.*;
import com.platfos.pongift.store.service.StoreService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.exception.ParamValidateException;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.model.ParamValidate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Validated
@RestController
@ParamClzz(name = "2. 매장")
public class StoreController {
    /** 매장 서비스 **/
    @Autowired
    StoreService service;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "매장 및 가맹점 리스트 조회")
    @RequestMapping(path="stores", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<Store> stores(HttpServletRequest request
            , @RequestParam(required = false) @SimIndex(simIndex = SIMIndex.MSM_STORE) @Description("프렌차이즈 본사 매장ID") String ownedId) throws Exception{
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        //본사 포함 하위 매장 리스트
        if(StringUtils.isEmpty(ownedId)) {
            return responseService.list(request, service.getStoreList(agencyId));
        }
        //대행사 하위 매장 리스트
        else {
            Store ownedStore = service.getStore(agencyId, ownedId);
            if(ownedStore != null){ //본사 매장ID를 본사 서비스ID로 교체
                ownedId = ownedStore.getServiceId();
            }
            return responseService.list(request, service.getStoreList(agencyId, ownedId));
        }
    }

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "본사 리스트 조회")
    @RequestMapping(path="store/owners", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<Store> groups(HttpServletRequest request) {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //본사 리스트 조회
        return responseService.list(request, service.getOwnerStoreList(agencyId));
    }

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "매장 정보 조회")
    @RequestMapping(path="store", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Store> store(HttpServletRequest request
            , @RequestParam @Require @SimIndex(simIndex = SIMIndex.MSM_STORE) @Description("매장ID") String storeId) {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //매장 정보 조회
        return responseService.data(request, service.getStore(agencyId, storeId));
    }

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "매장 등록")
    @Validated(GroupA.class)
    @RequestMapping(path="store", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<StoreId> putStore(HttpServletRequest request, @RequestBody @Valid Store store) throws Exception {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());

        //프렌차이즈 본사 service_id 체크
        if(!StringUtils.isEmpty(store.getOwnedId())){
            Store ownedStore = service.getStore(agencyId, store.getOwnedId());

            //존재 여부 체크
            if(ownedStore == null) throw new ParamValidateException(ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "ownedId", store.getOwnedId()));
            //본사 여부 체크
            else if(!StringUtils.isEmpty(ownedStore.getOwnedId())) throw new ParamValidateException(ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "ownedId", store.getOwnedId()));

            //본사 매장ID를 본사 서비스ID로 교체
            store.setOwnedId(ownedStore.getServiceId());
        }

        //매장 등록 기본 정보 설정
        store.setServiceId(null);
        service.setCustomDefaultStoreByAgency(agencyId, store);

        //매장등록
        Store newStore = service.insertStore(store);
        return responseService.data(request, StoreId.builder().storeId(newStore.getStoreId()).build());
    }

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "매장 정보 변경")
    @RequestMapping(path="store", method= RequestMethod.PUT, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse postStore(HttpServletRequest request, @RequestBody Store store) throws Exception {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //매장 정보 조회
        Store dbStore = service.getStore(agencyId, store.getStoreId());
        if(ObjectUtils.isEmpty(dbStore)){
            throw new APIPermissionAuthException("No API Permission Authorization : store/update => agencyId : "+agencyId+", storeId : "+store.getStoreId());
        }

        //프렌차이즈 본사 service_id 체크
        if(!StringUtils.isEmpty(store.getOwnedId())){
            Store ownedStore = service.getStore(agencyId, store.getOwnedId());

            //존재 여부 체크
            if(ownedStore == null) throw new ParamValidateException(ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "ownedId", store.getOwnedId()));
                //본사 여부 체크
            else if(!StringUtils.isEmpty(ownedStore.getOwnedId())) throw new ParamValidateException(ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "ownedId", store.getOwnedId()));

            //본사 매장ID를 본사 서비스ID로 교체
            store.setOwnedId(ownedStore.getServiceId());
        }

        //매장 변경 기본 정보 설정
        service.setCustomDefaultStoreByAgency(agencyId, store);

        //매장 정보 변경
        Store newStore = service.updateStore(store);
        if(newStore != null){
            return responseService.response(request, ResponseCode.SUCCESS);
        }else{
            return responseService.response(request, ResponseCode.FAIL);
        }
    }

    @APIPermission(roles = { APIRole.PRODUCT_SUPPLY_AGENCY })
    @ParamMethodValidate(methodName = "매장 서비스 해지")
    @RequestMapping(path="store/termination", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse storeTermination(HttpServletRequest request, @RequestBody StoreId storeId) throws Exception {
        //대행사 아이디
        String agencyId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //매장 정보 조회
        Store dbStore = service.getStore(agencyId, storeId.getStoreId());
        if(ObjectUtils.isEmpty(dbStore)){
            throw new APIPermissionAuthExceptionForNoLogger("No API Permission Authorization : store/termination => agencyId : "+agencyId+", storeId : "+storeId.getStoreId());
        }

        //매장 서비스 해지
        int rs = service.updateMIMServiceSt(agencyId, dbStore.getServiceId(), MIMServiceSt.TERMINATION);
        if(rs > 0){
            return responseService.response(request, ResponseCode.SUCCESS);
        }else{
            return responseService.response(request, ResponseCode.FAIL);
        }
    }
}
