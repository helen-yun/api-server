package com.platfos.pongift.goods.service;

import com.platfos.pongift.attach.service.AttachFileService;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.category.model.GoodsCategory;
import com.platfos.pongift.category.model.GoodsCategoryChannel;
import com.platfos.pongift.category.service.GoodsCategoryService;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.gim.model.GIMChannelInfo;
import com.platfos.pongift.data.gim.model.GIMExhibitInfo;
import com.platfos.pongift.data.gim.model.GIMGoodsControl;
import com.platfos.pongift.data.gim.service.GIMChannelInfoService;
import com.platfos.pongift.data.gim.service.GIMExhibitInfoService;
import com.platfos.pongift.data.gim.service.GIMGoodsControlService;
import com.platfos.pongift.data.gim.service.GIMGoodsService;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.definition.ExhibitSt;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.gateway.service.GWService;
import com.platfos.pongift.giftcard.model.GiftCardTemplateInfo;
import com.platfos.pongift.giftcard.service.GiftCardService;
import com.platfos.pongift.goods.dao.GoodsDAO;
import com.platfos.pongift.goods.exception.GoodsControlException;
import com.platfos.pongift.goods.exception.GoodsExhibitNoApprovalStException;
import com.platfos.pongift.goods.exception.GoodsExhibitStWaittingException;
import com.platfos.pongift.goods.exception.StoreServiceStException;
import com.platfos.pongift.goods.model.*;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.store.model.StoreOperate;
import com.platfos.pongift.store.service.StoreService;
import com.platfos.pongift.telegram.service.TelegramService;
import com.platfos.pongift.validate.model.ParamValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class GoodsService {
    /**
     * Application Properties
     */
    private final ApplicationProperties properties;

    /**
     * 시스템 정보 서비스
     **/
    private final SIMSystemInfoService systemInfoService;

    /**
     * 서비스 정보 서비스(DB)
     **/
    private final MIMServiceService mimServiceService;

    /**
     * 매장 정보 서비스
     **/
    private final StoreService storeService;

    /**
     * 상품권 서비스
     **/
    private final GiftCardService giftCardService;

    /**
     * 공통 기초 코드 서비스
     **/
    private final SIMCodeService simCodeService;

    /**
     * 상품 서비스(DB)
     **/
    private final GIMGoodsService gimGoodsService;

    /**
     * 상품 채널 서비스(DB)
     **/
    private final GIMChannelInfoService gimChannelInfoService;

    /**
     * 유통채널 상품 제어 서비스(DB)
     **/
    private final GIMGoodsControlService gimGoodsControlService;

    /**
     * 전시 카테고리 서비스(DB)
     **/
    private final GIMExhibitInfoService gimExhibitInfoService;

    /**
     * 상품(전시)카테고리
     **/
    private final GoodsCategoryService goodsCategoryService;

    /**
     * 유통채널 Gateway 서비스
     **/
    private final GWService gwService;

    /**
     * 첨부파일 서비스
     **/
    private final AttachFileService attachFileService;

    /**
     * 텔레그램 서비스
     **/
    private final TelegramService telegramService;

    /**
     * 조회용 상품 DAO
     **/
    private final GoodsDAO dao;

    /**
     * 상품의 부가정보 설정(카테고리, 유통채널 리스트)
     *
     * @param goods : 상품 정보 객체
     */
    private void setGoodsInfo(Goods goods) {
        if (goods == null) return;
        if (StringUtils.isEmpty(goods.getGoodsId())) return;

        //상품(전시) 카테고리 조회
        GoodsCategory category = goodsCategoryService.getGoodsCategoryCompriseWithoutExhibit(goods.getGoodsCategory());

        //상품(전시) 카테고리 지원 유통채널 리스트 조회
        List<GoodsCategoryChannel> channels = new ArrayList<>();
        List<GIMChannelInfo> gimChannelInfos = gimChannelInfoService.getGIMChannelInfoList(goods.getGoodsId(), null);
        if (!ObjectUtils.isEmpty(gimChannelInfos)) {
            for (GIMChannelInfo gimChannelInfo : gimChannelInfos) {
                channels.add(GoodsCategoryChannel.builder()
                        .channelGb(gimChannelInfo.getChannelGb())
                        .channelNm(gimChannelInfo.getChannelNm())
                        .build());
            }
        }
        //유통채널 리스트 설정
        category.setChannels(channels);
        //상품(전시) 카테고리 설정
        goods.setCategory(category);
    }

    /**
     * 상품 리스트(검색용)
     *
     * @param condition : 검색 조건 객체
     * @return
     */
    public GoodsList getGoodsList(GoodsSearchCondition condition) {
        //페이지 초기화
        int page = (condition.getPage() == null | (condition.getPage() != null && condition.getPage() < 1)) ? 1 : condition.getPage();
        //페이지 크기 초기화
        int pageSize = (condition.getPageSize() == null | (condition.getPageSize() != null && condition.getPageSize() < 1)) ? 20 : condition.getPageSize();
        pageSize = (pageSize > 100) ? 100 : pageSize;

        condition.setPage(page - 1);
        condition.setPageSize(pageSize);

        //상품(전시) 카테고리
        if (!StringUtils.isEmpty(condition.getGoodsCategoryCd())) {
            SIMCode category = simCodeService.getSIMCodeByGoodsCategoryCd(condition.getGoodsCategoryCd());
            condition.setGoodsCategory(category.getCodeId());
        }

        //시스템 모드 설정(개발/운영)
        condition.setAccessFl(properties.getSystemMode());

        //상품 리스트의 페이지 정보 조회
        int totalPage = dao.selectGoodsListTotalPage(condition);

        //상품 리스트 조회
        List<Goods> goodsList = dao.selectGoodsList(condition);
        for (Goods goods : goodsList) {
            //부가 정보 설정
            setGoodsInfo(goods);
        }

        //상품 응답 리스트 생성
        return GoodsList.builder()
                .page(Page.builder().page(page).totalPage(totalPage).build())
                .goods(goodsList).build();
    }

    /**
     * 상품 상세 정보 조회
     *
     * @param goodsId : 상품 아이디
     * @return
     */
    public Goods getGoods(String goodsId) {
        return getGoods(null, goodsId);
    }

    /**
     * 상품 상세 정보 조회
     *
     * @param agencyId : 상품공급 대행사 서비스 아이디
     * @param goodsId  : 상품 아이디
     * @return
     */
    public Goods getGoods(String agencyId, String goodsId) {
        //상품 정보 조회
        Goods goods = dao.selectGoodsByAgencyIdAndGoodsId(agencyId, goodsId, properties.getSystemMode());
        setGoodsInfo(goods);
        return goods;
    }

    /**
     * 상품 상세 정보 조회
     *
     * @param ledgerId : 상품권 원장 아이디
     * @return
     */
    public Goods getGoodsByLedgerId(String ledgerId) {
        //상품 정보 조회
        Goods goods = dao.selectGoodsByLedgerId(ledgerId, null, properties.getSystemMode(), TradeTp.SALES.getCode());
        setGoodsInfo(goods);
        return goods;
    }

    /**
     * 상품 or 직발송 상세 정보 조회
     *
     * @param ledgerId : 상품권 원장 아이디
     * @return
     */
    public Goods getGoodsORSendGoodsByLedgerId(String ledgerId) {
        Goods goods = dao.selectGoodsByLedgerId(ledgerId, null, properties.getSystemMode(), TradeTp.SALES.getCode());
        if (goods != null) {
            setGoodsInfo(goods);
        } else {
            goods = dao.selectGoodsByLedgerId(ledgerId, null, properties.getSystemMode(), TradeTp.SEND_DIRECT.getCode());
        }
        return goods;

    }

    /**
     * 상품의 부가 요청 정보 설정(insert/update용)
     *
     * @param goods : 상품 정보 객체
     */
    public void setGoodsRequest(Goods goods) {
        //유효기간 구분 설정
        if (StringUtils.isEmpty(goods.getExpiryGb())) goods.setExpiryGb("01");

        //상품(전시) 카테고리 조회/설정
        GoodsCategory category = goodsCategoryService.getGoodsCategoryByGoodsCategoryCd(goods.getCategory().getGoodsCategoryCd(), null);
        goods.setGoodsCategory(category.getGoodsCategory());
    }

    /**
     * 상품 정보 등록
     *
     * @param goods : 상품 정보 객체
     * @return 생성된 상품 아이디
     * @throws Exception
     */
    public String insertGoods(Goods goods) throws Exception {
        //상품의 부가 요청 정보 설정
        setGoodsRequest(goods);

        //상품 정보 등록
        String goodsId = gimGoodsService.insertGIMGoods(goods.gimGoods());
        goods.setGoodsId(goodsId);

        if (!StringUtils.isEmpty(goodsId)) {
            //상품 채널 정보 저장
            saveChannelInfo(goods);
        }

        return goodsId;
    }

    /**
     * 상품 정보 변경
     *
     * @param goods : 상품 정보 객체
     * @return : 변경된 상품의 결과 수
     * @throws Exception
     */
    public int updateGoods(Goods goods, boolean isApproved) throws Exception {
        // 상품의 부가 요청 정보 설정
        setGoodsRequest(goods);

        // 상품 정보 변경
        int result = gimGoodsService.updateGIMGoods(goods.gimGoods());

        if (result > 0) {
            // 상품 채널 정보 저장
            saveChannelInfo(goods);

            if (isApproved) {
                // 채널 서비스(제어) 정보
                // 어드민에서 승인 시행
                gimGoodsControlService.insertGIMGoodsControls(goods.gimGoodsControls());
            }
        }
        return result;
    }

    /**
     * 상품 채널 정보 저장
     *
     * @param goods : 상품 정보 객체
     * @throws Exception
     */
    private void saveChannelInfo(Goods goods) throws Exception {
        //db 정보
        List<GIMChannelInfo> dbGimChannelInfos = gimChannelInfoService.getGIMChannelInfoList(goods.getGoodsId(), null);

        //삭제 리스트 생성
        List<GIMChannelInfo> deleteChannelInfos = new ArrayList<>();
        for (GIMChannelInfo dbInfo : dbGimChannelInfos) {
            boolean isDelete = true;
            for (GIMChannelInfo gimChannelInfo : goods.gimChannelInfos()) {
                if (gimChannelInfo.getChannelGb().equals(dbInfo.getChannelGb())) {
                    isDelete = false;
                    break;
                }
            }
            if (isDelete) {
                if (dbInfo.getUseFl().equals("Y")) {
                    deleteChannelInfos.add(dbInfo);
                }
            }
        }

        //정보 저장(insert/update)
        for (GIMChannelInfo gimChannelInfo : goods.gimChannelInfos()) {
            boolean isNew = true;
            gimChannelInfo.setUseFl("Y");

            for (GIMChannelInfo dbInfo : dbGimChannelInfos) {
                if (gimChannelInfo.getChannelGb().equals(dbInfo.getChannelGb())) {
                    gimChannelInfoService.updateGIMChannelInfoUseFl(gimChannelInfo);
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                // 수기발송인경우 channelId 자동생성
                if (ChannelGb.PLATFOS.getCode().equals(gimChannelInfo.getChannelGb())) {
                    gimChannelInfo.setChannelId(goods.getGoodsId());
                }
                String routeId = gimChannelInfoService.insertGIMChannelInfo(gimChannelInfo);
                gimChannelInfo.setRouteId(routeId);
            }
        }

        //삭제(delete)
        for (GIMChannelInfo delInfo : deleteChannelInfos) {
            delInfo.setUseFl("N");
            gimChannelInfoService.updateGIMChannelInfoUseFl(delInfo);
        }
    }

    /**
     * 상품 전시 상태 변경
     *
     * @param goods : 상품 객체
     * @return 변경된 결과 수
     */
    public int updateGoodsExhibitSt(Goods goods) throws Exception {
        ExhibitSt exhibitSt = null;
        if (goods.getExhibitSt().equals("Y")) exhibitSt = ExhibitSt.EXHIBITION_SALE;
        else if (goods.getExhibitSt().equals("N")) exhibitSt = ExhibitSt.STOP_SELLING;

        if (exhibitSt == null) return 0;

        int rs = gimGoodsService.updateGIMGoodsExhibitSt(goods.getGoodsId(), exhibitSt);
        if (rs > 0) {
            gimGoodsControlService.insertGIMGoodsControls(goods.gimGoodsControls());
        }
        return rs;
    }


    /**
     * 상품 리스트 검색 조건 유효성 검사
     *
     * @param condition
     * @return
     */
    public ParamValidate validate(GoodsSearchCondition condition) {
        //상품(전시) 카테고리 검사
        if (!StringUtils.isEmpty(condition.getGoodsCategoryCd())) {
            SIMCode category = simCodeService.getSIMCodeByGoodsCategoryCd(condition.getGoodsCategoryCd());
            if (category == null) {
                return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "", "Illegal Code Value : 상품 카테고리 코드");
            }
        }
        return ParamValidate.buildSuccess();
    }

    /**
     * 상품 관리(생성/변경) 유효성 검사
     *
     * @param goods
     * @return
     */
    public ParamValidate validate(Goods goods) {
        //상품(전시) 카테고리 검사
        GoodsCategory category = goods.getCategory();
        if (category == null) {
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "category");
        }

        if (StringUtils.isEmpty(category.getGoodsCategoryCd())) {
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "category.goodsCategoryCd");
        }
        if (ObjectUtils.isEmpty(category.getChannels())) {
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "category.channels");
        }

        for (GoodsCategoryChannel channel : category.getChannels()) {
            if (StringUtils.isEmpty(channel.getChannelGb()))
                return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "category.channels.channelGb");
        }

        GoodsCategory dbCategory = goodsCategoryService.getGoodsCategoryByGoodsCategoryCd(category.getGoodsCategoryCd(), null);
        if (dbCategory == null) {
            return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "category.goodsCategoryCd", category.getGoodsCategoryCd(), "지원하지 않는 상품 카테고리 코드");
        }
        for (GoodsCategoryChannel channel : category.getChannels()) {
            boolean isCheckOK = false;
            for (GoodsCategoryChannel dbChannel : dbCategory.getChannels()) {
                if (dbChannel.getChannelGb().equals(channel.getChannelGb())) {
                    isCheckOK = true;
                    break;
                }
            }
            if (!isCheckOK) {
                return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "category.channels.channelGb", channel.getChannelGb(), "지원하지 않는 유통 채널 구분");
            }
        }

        //상품(전시) 금액 검사
        if (goods.getSalePrice() % 10 > 0) {
            return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "salePrice", goods.getSalePrice(), "10원 미만 단위 처리불가");
        }
        if (goods.getRetailPrice() % 10 > 0) {
            return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "retailPrice", goods.getRetailPrice(), "10원 미만 단위 처리불가");
        }
        if (goods.getSupplyPrice() % 10 > 0) {
            return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "supplyPrice", goods.getSupplyPrice(), "10원 미만 단위 처리불가");
        }

        return ParamValidate.buildSuccess();
    }

    /**
     * 상품 처리 상태 체크
     *
     * @param goods
     * @throws Exception
     */
    public void checkGoodsProcessSt(Goods goods) throws Exception {
        ProcessSt processSt = ProcessSt.findByCode(goods.getProcessSt());
        if (processSt != null && (processSt == ProcessSt.WORKING | processSt == ProcessSt.ERROR)) { //유통채널 제어 상태 확인
            List<GIMGoodsControl> goodsControls =
                    (processSt == ProcessSt.WORKING) ?
                            gimGoodsControlService.getGIMGoodsControls(goods.getGoodsId(), ProcessSt.READY, processSt) :
                            gimGoodsControlService.getGIMGoodsControls(goods.getGoodsId(), processSt);
            String channelName = null;
            if (!ObjectUtils.isEmpty(goodsControls)) {
                SIMCode simCode = simCodeService.getSIMCode(SIMCodeGroup.channelGb, goodsControls.get(0).getChannelGb());
                channelName = simCode.getCodeNm();
            }
            String goodsName = goods.getGoodsNm();

            throw new GoodsControlException(
                    (processSt == ProcessSt.WORKING) ? ResponseCode.FAIL_GOODS_CHANNEL_WORKING : ResponseCode.FAIL_GOODS_CHANNEL_ERROR,
                    channelName,
                    goodsName);
        }
    }

    /**
     * 매장 서비스 상태 체크
     *
     * @param store
     * @throws StoreServiceStException
     */
    public void checkStoreServiceSt(Store store) throws StoreServiceStException {
        MIMService mimService = mimServiceService.getMIMService(store.getServiceId());

        MIMServiceSt serviceSt = MIMServiceSt.findByCode(mimService.getServiceSt());
        if (serviceSt != MIMServiceSt.ACTIVE) { //서비스 활성화 상태
            SIMCode simCode = simCodeService.getSIMCode(SIMCodeGroup.serviceSt, serviceSt.getCode());
            throw new StoreServiceStException(store.getStoreNm(), simCode.getCodeNm());
        }
    }

    /**
     * 상품 전시 상태 체크(전시 대기)
     *
     * @param goods
     * @throws GoodsExhibitStWaittingException
     */
    public void checkGoodsExhibitStWaitting(Goods goods) throws GoodsExhibitStWaittingException {
        ExhibitSt exhibitSt = ExhibitSt.findByCode(goods.getExhibitSt());
        if (!(exhibitSt != null && exhibitSt != ExhibitSt.WAITING_FOR_SALES)) { //상품 전시 상태 확인
            throw new GoodsExhibitStWaittingException(goods.getGoodsNm());
        }
    }

    /**
     * 상품 승인 상태 체크(승인)
     *
     * @param goods
     * @throws GoodsExhibitNoApprovalStException
     */
    public void checkGoodsExhibitApprovalSt(Goods goods) throws GoodsExhibitNoApprovalStException {
        GoodsApprovalSt goodsApprovalSt = GoodsApprovalSt.findByCode(goods.getApprovalSt());
        if (!(goodsApprovalSt != null && goodsApprovalSt == GoodsApprovalSt.APPROVAL)) { //상품 승인 상태 확인
            throw new GoodsExhibitNoApprovalStException(goods.getGoodsNm());
        }
    }

    /**
     * 상품 매장 정보 일치 확인
     *
     * @param goodsId
     * @param storeId
     * @return count
     * @throws APIPermissionAuthException
     */
    public void checkGoodsIdAndStoreId(String goodsId, String storeId) throws ValidationException {
        long count = dao.countGoodsIdAndStoreId(goodsId, storeId);
        if (count <= 0) {
            throw new ValidationException("상품 권한 없음 : " + goodsId );
        }
    }

    /**
     * 유통채널 상품 탬플릿 내용 조회
     *
     * @param store 매장
     * @param goods 상품
     * @return
     * @throws Exception
     */
    public String html(Store store, Goods goods, MIMService mimService) throws Exception {
        //탬플릿 요청 파라메터 생성
        Map<String, String> params = makeTemplateParams(store, goods, mimService);
        if (params == null) return null;

        //상품 탬플릿 조회
        GiftCardTemplateInfo template = giftCardService.generateGiftCard(params, true);
        //html code
        return template.getTemplate().getHtmlCd();
    }

    /**
     * 유통채널 상품 탬플릿 내용 조회(이미지)
     *
     * @param store
     * @param goods
     * @return
     * @throws Exception
     */
    public byte[] getTemplateBytes(Store store, Goods goods, MIMService mimService) throws Exception {
        //탬플릿 요청 파라메터 생성
        Map<String, String> params = makeTemplateParams(store, goods, mimService);
        if (params == null) return null;
        GiftCardTemplateInfo template = giftCardService.generateGiftCard(params, true);
        //image byte array
        return template.getBytes();
    }

    /**
     * 탬플릿 요청 파라메터 생성
     *
     * @param store
     * @param goods
     * @return
     */
    public Map<String, String> makeTemplateParams(Store store, Goods goods, MIMService mimService) {
        Map<String, String> template = new HashMap<>();
        template.put("templateId", "TF200625000006");

        boolean hideStoreNm = false;
        boolean hideStoreAddress = false;
        boolean hideStoreOperates = false;
        boolean hideGoodsExchangePlace = false;
        boolean hideGoodsInfo = false;
        boolean hideGoodsCautionPoint = false;
        boolean hideGoodsLimitPoint = false;
        boolean hideServiceUrl = true;

        if (store != null) {
            //매장 이름
            template.put("storeNm", store.getStoreNm());
            hideStoreNm = StringUtils.isEmpty(store.getStoreNm());

            //매장 주소
            String storeAddress = store.getStoreAddress1() + (StringUtils.isEmpty(store.getStoreAddress1()) ? "" : " ") + store.getStoreAddress2();
            template.put("storeAddress", storeAddress);

            // 서비스구분값이 OFFLINE 인 경우에만 노출
            if (mimService.getServiceGb().equals(MIMServiceGb.OFFLINE.getCode())) {
                hideStoreAddress = StringUtils.isEmpty(storeAddress);
            } else {
                hideStoreAddress = true;
            }

            // 사이트 (URL)
            template.put("serviceUrl", mimService.getServiceUrl());
            // 서비스구분값이 ONLINE 인 경우 사이트 (URL) 노출
            if (mimService.getServiceGb().equals(MIMServiceGb.ONLINE.getCode())) {
                hideServiceUrl = StringUtils.isEmpty(mimService.getServiceUrl());
            }

            //운영 시간
            String storeOperates = "";
            if (!ObjectUtils.isEmpty(store.getOperates())) {
                for (int i = 0; i < store.getOperates().size(); i++) {
                    StoreOperate operate = store.getOperates().get(i);

                    String dayGb = (!StringUtils.isEmpty(operate.getDayGb())) ? simCodeService.getSIMCode(SIMCodeGroup.dayGb, operate.getDayGb()).getCodeNm() : "";
                    String timeTp = "";
                    if (!StringUtils.isEmpty(operate.getTimeTp())) {
                        SIMCode simTimeTp = simCodeService.getSIMCode(SIMCodeGroup.timeTp, operate.getTimeTp());
                        MSMOperateTimeTp msmTimeTp = MSMOperateTimeTp.findByCode(simTimeTp.getCodeCd());
                        if (msmTimeTp != null && msmTimeTp == MSMOperateTimeTp.CLOSED) timeTp = simTimeTp.getCodeNm();
                    }

                    if (StringUtils.isEmpty(timeTp)) {
                        String openingTm = (!StringUtils.isEmpty(operate.getOpeningTm())) ? simCodeService.getSIMCode(SIMCodeGroup.openingTm, operate.getOpeningTm()).getCodeNm() : "";
                        String endingTm = (!StringUtils.isEmpty(operate.getEndingTm())) ? simCodeService.getSIMCode(SIMCodeGroup.endingTm, operate.getEndingTm()).getCodeNm() : "";
                        timeTp = ((!StringUtils.isEmpty(operate.getTimeTp()) & !StringUtils.isEmpty(operate.getTimeTp())) ? openingTm + " ~ " + endingTm : "");
                    }
                    storeOperates += dayGb + " : " + timeTp + ((i < store.getOperates().size() - 1) ? "<br>" : "");
                }
            }
            template.put("storeOperates", storeOperates);
            hideStoreOperates = StringUtils.isEmpty(storeOperates);
        } else {
            hideStoreNm = true;
            hideStoreAddress = true;
            hideStoreOperates = true;
        }

        //상품권 사용처
        template.put("goodsExchangePlace", goods.getExchangePlace());
        hideGoodsExchangePlace = StringUtils.isEmpty(goods.getExchangePlace());

        // 상품권 정보
        template.put("goodsInfo", goods.getGoodsInfo());
        hideGoodsInfo = StringUtils.isEmpty(goods.getGoodsInfo());

        // 주의사항
        template.put("goodsCautionPoint", goods.getCautionPoint());
        hideGoodsCautionPoint = StringUtils.isEmpty(goods.getCautionPoint());

        // 제한사항
        template.put("goodsLimitPoint", goods.getLimitPoint());
        hideGoodsLimitPoint = StringUtils.isEmpty(goods.getLimitPoint());

        //각 항목의 노출 여부 설정(css display 속성값)
        //매장 이름 노출 여부
        if (hideStoreNm) template.put("storeNmDP", "none");
        else template.put("storeNmDP", "");

        //매장 주소 노출 여부
        if (hideStoreAddress) template.put("storeAddressDP", "none");
        else template.put("storeAddressDP", "");

        // 사이트 주소 노출 여부
        if (hideServiceUrl) template.put("serviceUrlDP", "none");
        else template.put("serviceUrlDP", "");

        //운영시간 노출 여부
        if (hideStoreOperates) template.put("storeOperatesDP", "none");
        else template.put("storeOperatesDP", "");

        //상품권 사용처 노출 여부
        if (hideGoodsExchangePlace) template.put("goodsExchangePlaceDP", "none");
        else template.put("goodsExchangePlaceDP", "");

        // 상품권 정보 노출 여부
        if (hideGoodsInfo) template.put("goodsInfoDP", "none");
        else template.put("goodsInfoDP", "");

        // 주의사항 노출 여부
        if (hideGoodsCautionPoint) template.put("goodsCautionPointDP", "none");
        else template.put("goodsCautionPointDP", "");

        // 제한사항 노출 여부
        if (hideGoodsLimitPoint) template.put("goodsLimitPointDP", "none");
        else template.put("goodsLimitPointDP", "");

        //상품 내용 전체 노출 여부
        if (hideStoreNm & hideStoreAddress & hideStoreOperates & hideGoodsExchangePlace & hideGoodsInfo & hideGoodsCautionPoint & hideGoodsLimitPoint & hideServiceUrl) {
            template.put("contentsDP", "none");
        } else template.put("contentsDP", "");

        return template;
    }

    /**
     * 상품 내용 이미지 저장
     *
     * @param store 매장
     * @param goods 상품
     * @throws Exception
     */
    public void saveGoodsPreview(Store store, Goods goods, MIMService mimService) throws Exception {
        byte[] bytes = getTemplateBytes(store, goods, mimService);
        String fileName = goods.getGoodsId() + ".jpg";
        String path = "/goods/";

        attachFileService.uploadKeepOriginalFile(path + fileName, "image/jpeg", bytes);
    }

    /**
     * 유통채널 상품 판매 전시 만료일 연장
     *
     * @throws Exception
     */
    public void extensionExpirationExhibitGoods() throws Exception { //상품 전시 상태 변경(전시 판매 만료일 연장)
        //전시 판매 만료 예정 채널 정보 조회
        List<GIMChannelInfo> expirations = gimChannelInfoService.getGIMChannelInfoExpirationExhibitEndList();

        String url = "goods/exhibitSt";
        String method = "POST";

        for (GIMChannelInfo expiration : expirations) {
            ParamValidate validate = gwService.validate(url, method, expiration.getChannelGb());
            if (!validate.hasError()) {
                //상품 전시 상태 변경(전시 판매 만료일 연장)
                GIMChannelInfo channelInfo = gimChannelInfoService.getGIMChannelInfo(expiration.getRouteId(), null);
                GWGoodsExhibitSt request = GWGoodsExhibitSt.builder()
                        .goodsId(channelInfo.getChannelId())
                        .channelGb(channelInfo.getChannelGb())
                        .exhibitSt(channelInfo.getExhibitSt())
                        .build();

                HttpEntity httpEntity = gwService.createHttpEntity(method, MediaType.APPLICATION_JSON, request);
                GWResponseWrapper<GWGoodsResponse> rsExhibitSt = gwService.send(GWGoodsResponse.class, channelInfo.getChannelGb(), url, method, httpEntity);

                if (rsExhibitSt.getResponse().isSuccess()) {
                    //상품 채널 정보 업데이트(전시 상태, 전시 종료일) 시작
                    String gwGoodsURL = "goods";
                    String gwGoodsMethod = "GET";

                    ParamValidate gwGoodsValidate = gwService.validate(gwGoodsURL, gwGoodsMethod, channelInfo.getChannelGb());
                    if (!gwGoodsValidate.hasError()) {
                        HttpEntity gwGoodsHttpEntity = gwService.createHttpEntity(gwGoodsMethod, null, GWGoodsId.builder().goodsId(request.getGoodsId()).build());
                        GWResponseWrapper<GWGoodsResponse> rsGoods = gwService.send(GWGoodsResponse.class, request.getChannelGb(), gwGoodsURL, gwGoodsMethod, gwGoodsHttpEntity);
                        if (rsGoods.getResponse().isSuccess()) {
                            GWGoodsResponse gwGoodsResponse = rsGoods.getData();
                            if (gwGoodsResponse != null) {
                                gimChannelInfoService.updateGIMChannelInfoExhibit(GIMChannelInfo.builder()
                                        .goodsId(channelInfo.getGoodsId())
                                        .channelGb(request.getChannelGb())
                                        .exhibitSt(gwGoodsResponse.getExhibitSt())
                                        .exhibitEnd(gwGoodsResponse.getSaleEndDate())
                                        .build());
                            }
                        }
                    }
                    //상품 채널 정보 업데이트(전시 상태, 전시 종료일) 끝
                }
            }
        }
    }

    /**
     * 유통채널 상품 정보 반영
     *
     * @throws Exception
     */
    public void goodsScheduler() throws Exception {
        log.info("==================================================");
        log.info("유통채널 상품 정보 반영 시작 goodsScheduler");
        log.info("//================================================");
        //상품 아이디 조회
        String goodsId = gimGoodsControlService.getGoodsIdForScheduler();
        if (StringUtils.isEmpty(goodsId)) return;
        //상품 조회
        Goods goods = dao.selectGoodsByAgencyIdAndGoodsId(null, goodsId, properties.getSystemMode());
        if (goods == null) {
            log.error("goodsScheduler : 상품 정보 미존재");
            return;
        }
        Store store = storeService.getStoreByGoodsId(goodsId);
        if (store == null) {
            log.error("goodsScheduler : 매장 정보 미존재");
            return;
        }

        //상품 정보 반영 스케쥴 리스트업(Queue)
        List<GIMGoodsControl> controls = gimGoodsControlService.getGIMGoodsControlsForScheduler(goodsId);

        //상품 처리 상태 '진행 중' 업데이트
        gimGoodsService.updateGIMGoodsProcessSt(goodsId, ProcessSt.WORKING);
        //상품 정보 반영 '진행 중' 업데이트
        gimGoodsControlService.updateGIMGoodsControlProcessStForScheduler(goodsId, ProcessSt.WORKING);

        //Queue 내용 처리 시작
        boolean finalSuccess = true;
        for (GIMGoodsControl control : controls) {
            //채널 정보 조회
            GIMChannelInfo channelInfo = gimChannelInfoService.getGIMChannelInfo(control.getGoodsId(), control.getChannelGb(), null);

            boolean isUpdate = StringUtils.isNotEmpty(channelInfo.getChannelId());
            String url = "goods";
            String method = isUpdate ? "PUT" : "POST";
            String channelGb = control.getChannelGb();

            //이력 정보 저장('진행중')
            gimGoodsControlService.insertGIMGoodsControlHisForScheduler(goodsId, channelGb, ProcessSt.WORKING.getCode());

            ParamValidate validate = gwService.validate(url, method, channelGb);
            if (!validate.hasError()) {
                //상품 전시 상태
                String exhibitSt = goods.getExhibitSt();
                ExhibitSt goodsExhibitSt = ExhibitSt.findByCode(exhibitSt);
                if (goodsExhibitSt == null) goodsExhibitSt = ExhibitSt.STOP_SELLING;
                else if (goodsExhibitSt == ExhibitSt.WAITING_FOR_SALES) goodsExhibitSt = ExhibitSt.STOP_SELLING;
                exhibitSt = goodsExhibitSt.getCode();

                //유통채널 미사용 설정 시, 판매 중지 상태로 설정
                if (channelInfo.getUseFl().equals("N")) exhibitSt = ExhibitSt.STOP_SELLING.getCode();

                //상품 카테고리 맵핑
                List<GIMExhibitInfo> exhibitInfos = gimExhibitInfoService.getGIMExhibitInfos(goods.getGoodsCategory(), channelGb);
                List<String> categories = new ArrayList<>();
                for (GIMExhibitInfo exhibitInfo : exhibitInfos) {
                    categories.add(exhibitInfo.getChannelCategory());
                }

                //상품 상세 정보 생성 시작
                String html = createGoodsInfoImageElement(goods, goodsId);

                //매장 이미지
                String host = systemInfoService.getValue(SystemKey.CDN_SERVER_HOST);
                String storeUrl = host + "/goods/" + goodsId + ".jpg";

                //유통채널 상품 정보 생성
                GWGoodsRequest request = GWGoodsRequest.builder()
                        .categories(categories)
                        .goodsNm(goods.getGoodsNm())
                        .goodsId(goodsId)
                        .stockCnt(channelInfo.getStockCnt())
                        .salePrice(goods.getSalePrice())
                        .retailPrice(goods.getRetailPrice())
                        .discountPrice(getDiscountPrice(goods))
                        .exhibitSt(exhibitSt)
                        .goodsImage(goods.getGoodsImage())
                        .infoUrl(goods.getGoodsDetailImage())
                        .storeUrl(storeUrl)
                        .expiryGb(goods.getExpiryGb())
                        .html(html)
                        .storeId(store.getStoreId())
                        .storeName(store.getStoreNm())
                        .storeSeq(store.getMarketplaceStoreSeq())
                        .build();

                GWResponseWrapper<?> result = null;
                if (!isUpdate) { //유통채널 상품 정보 등록
                    HttpEntity httpEntity = gwService.createHttpEntity(method, MediaType.APPLICATION_JSON, request);
                    result = gwService.send(GWGoodsId.class, channelGb, url, method, httpEntity);
                } else { //유통채널 상품 정보 변경
                    // request.setGoodsId(goodsId);
                    // 20200707 dkchoi75
                    request.setGoodsId(channelInfo.getChannelId());
                    HttpEntity httpEntity = gwService.createHttpEntity(method, MediaType.APPLICATION_JSON, request);
                    result = gwService.send(null, channelGb, url, method, httpEntity);
                }

                //유통채널 상품 정보 반영 정보 결과 처리
                boolean isSuccess = result.getResponse().isSuccess();
                ProcessSt processSt = (isSuccess) ? ProcessSt.COMPLETED : ProcessSt.ERROR;
                if (finalSuccess) finalSuccess = isSuccess;

                if (!isSuccess) {
                    telegramService.asyncSendMessage(
                            "유통채널 상품 정보 반영 실패 : " + System.lineSeparator() + System.lineSeparator() +
                                    "goodsId : " + control.getGoodsId() + System.lineSeparator() +
                                    "channelGb : " + control.getChannelGb() + System.lineSeparator() +
                                    result.getResponse().getMessage());
                }

                //상품 채널 정보 업데이트(유통채널 측 GoodsId(channelId)) 시작
                if (!isUpdate) { //상품 등록 시에만
                    if (isSuccess) { //상품 등록 성공 시에만
                        GWGoodsId gwGoodsId = (GWGoodsId) result.getData();
                        gimChannelInfoService.updateGIMChannelInfoChannelId(GIMChannelInfo.builder().goodsId(goodsId).channelGb(channelGb).channelId(gwGoodsId.getGoodsId()).build());
                        request.setGoodsId(gwGoodsId.getGoodsId());
                    }
                }
                //상품 채널 정보 업데이트(유통채널 측 GoodsId(channelId)) 끝

                //상품 채널 정보 업데이트(전시 상태, 전시 종료일) 시작
                String gwGoodsURL = "goods";
                String gwGoodsMethod = "GET";

                ParamValidate gwGoodsValidate = gwService.validate(gwGoodsURL, gwGoodsMethod, channelGb);
                if (!gwGoodsValidate.hasError() & !StringUtils.isEmpty(request.getGoodsId())) {
                    HttpEntity httpEntity = gwService.createHttpEntity(gwGoodsMethod, null, GWGoodsId.builder().goodsId(request.getGoodsId()).build());
                    GWResponseWrapper<GWGoodsResponse> rsGoods = gwService.send(GWGoodsResponse.class, channelGb, gwGoodsURL, gwGoodsMethod, httpEntity);
                    if (rsGoods.getResponse().isSuccess()) {
                        GWGoodsResponse gwGoodsResponse = rsGoods.getData();
                        log.info("gwGoodsResponse : {} ", gwGoodsResponse);
                        if (gwGoodsResponse != null) {
                            gimChannelInfoService.updateGIMChannelInfoExhibit(GIMChannelInfo.builder()
                                    .goodsId(goodsId)
                                    .channelGb(channelGb)
                                    .exhibitSt(gwGoodsResponse.getExhibitSt())
                                    .exhibitEnd(gwGoodsResponse.getSaleEndDate())
                                    .build());
                        }
                    }
                }
                //상품 채널 정보 업데이트(전시 상태, 전시 종료일) 끝


                //상품 정보 반영 결과 업데이트
                gimGoodsControlService.updateGIMGoodsControlProcessSt(control.getControlId(), processSt);

                //Queue 삭제
                if (isSuccess) gimGoodsControlService.deleteGIMGoodsControl(control.getControlId());
            } else {
                //상품 정보 반영 '미지원 채널' 업데이트
                gimGoodsControlService.updateGIMGoodsControlProcessSt(control.getControlId(), ProcessSt.UNSURPPORTEDCHANNEL);
                //Queue 삭제
                gimGoodsControlService.deleteGIMGoodsControl(control.getControlId());
            }
        }

        if (finalSuccess) {
            gimGoodsService.updateGIMGoodsProcessSt(goodsId, ProcessSt.COMPLETED);
        } else {
            gimGoodsService.updateGIMGoodsProcessSt(goodsId, ProcessSt.ERROR);
        }

    }

    /**
     * 상품 내용 이미지 저장(비동기)
     *
     * @param store 매장
     * @param goods 상품
     * @throws Exception
     */
    @Async
    public void asyncSaveGoodsPreview(Store store, Goods goods, MIMService mimService) throws Exception {
        saveGoodsPreview(store, goods, mimService);
    }

    /**
     * 승인된 모든 상품 내용 이미지 저장(비동기)
     */
    @Async
    public void asyncApprovedSaveGoodsPreview() {
        try {
            List<Goods> goodsList = dao.selectGoodsList(GoodsSearchCondition.builder().accessFl(properties.getSystemMode()).approvalSt(GoodsApprovalSt.APPROVAL.getCode()).build());
            for (Goods goods : goodsList) {
                Store store = storeService.getStore(goods.getStoreId());
                MIMService mimService = mimServiceService.getMIMService(goods.getServiceId());
                saveGoodsPreview(store, goods, mimService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 유통채널 상품 정보 반영(비동기)
     */
    @Async
    public void asyncGoodsScheduler() {
        try {
            goodsScheduler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 유통채널 상품 판매 전시 만료일 연장(비동기)
     */
    @Async
    public void asyncExtensionExpirationExhibitGoods() {
        try {
            extensionExpirationExhibitGoods();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GWResponseWrapper<GWGoodsResponse> setChannelSoldOut(String channelGb, String channelId) {
        HttpEntity<?> httpEntity = gwService.createHttpEntity(
                HttpMethod.POST.name()
                , null
                , GWGoodsExhibitSt.builder()
                        .channelGb(channelGb)
                        .channelId(channelId)
                        .exhibitSt(ExhibitSt.SOLD_OUT.getCode())
                        .build());
        return gwService.send(GWGoodsResponse.class, channelGb, "goods/exhibitSt", HttpMethod.POST.name(), httpEntity);
    }

    public void updateGIMGoodsStock(String channelGb, String goodsId, int orderCnt) {
        gimGoodsService.updateGIMGoodsStock(channelGb, goodsId, orderCnt);
    }

    public void updateGIMGoodsStockAndSoldOut(String goodsId) {
        gimGoodsService.updateGIMGoodsStockAndSoldOut(goodsId);
    }

    public void updateGIMGoodsMarketStock(String channelGb, String goodsId) {
        gimGoodsService.updateGIMGoodsMarketStock(channelGb, goodsId);
    }

    public GoodsChannel getGIMGoodsChannel(String channelGb, String goodsId) {
        return gimGoodsService.getGIMGoodsChannel(channelGb, goodsId);
    }

    /**
     * 상품 상세 정보 이미지 HTML Element 생성
     *
     * @param goods
     * @param goodsId
     * @return String
     */
    private String createGoodsInfoImageElement(Goods goods, String goodsId) {
        //상품 상세 정보 생성 시작
        String html = "";
        //상품 기본 정보 이미지 삽입 (공통상세템플릿 A)
        html += "<img width='100%' src='" + systemInfoService.getValue(SystemKey.CDN_SERVER_HOST) + "/goods/" + goodsId + ".jpg'/><br>";
        //상품 공통 이미지 삽입 (공통상세템플릿 B)
        html += "<img width='100%' src='" + systemInfoService.getValue(SystemKey.GOODS_COMMON_IMAGE) + "'/><br>";
        //상품 상세 이미지 삽입
        if (!StringUtils.isEmpty(goods.getGoodsDetailImage())) {
            html += "<img width='100%' src='" + goods.getGoodsDetailImage() + "'/><br>";
        }
        //상품 상세 정보 생성 끝
        return html;
    }

    /**
     * 스마트스토어 할인가 세팅
     *
     * @param goods
     * @return
     */
    private int getDiscountPrice(Goods goods) {
        int discountPrice = 0;
        if (goods.getRetailPrice() > goods.getSalePrice()) {
            discountPrice = goods.getRetailPrice() - goods.getSalePrice();
        }
        return discountPrice;
    }
}
