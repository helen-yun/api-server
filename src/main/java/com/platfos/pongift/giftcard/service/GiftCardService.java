package com.platfos.pongift.giftcard.service;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.platfos.pongift.data.cim.model.CIMLedger;
import com.platfos.pongift.data.cim.service.CIMLedgerService;
import com.platfos.pongift.data.mim.model.MIMService;
import com.platfos.pongift.data.mim.service.MIMServiceService;
import com.platfos.pongift.data.pom.service.POMDispatchInfoService;
import com.platfos.pongift.data.sim.model.*;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMPolicyInfoService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.data.sim.service.SIMTemplateInfoService;
import com.platfos.pongift.data.sim.service.SIMTransmitInfoService;
import com.platfos.pongift.data.ssm.model.SSMSmsTemplateValues;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.ghostdriver.model.CaptureConfig;
import com.platfos.pongift.ghostdriver.service.GhostDriver;
import com.platfos.pongift.giftcard.exeption.GiftException;
import com.platfos.pongift.giftcard.model.GiftCardTemplateInfo;
import com.platfos.pongift.giftcard.model.ResendGiftCard;
import com.platfos.pongift.giftcard.model.SMSTemplate;
import com.platfos.pongift.quantum.service.QuantumService;
import com.platfos.pongift.security.AES256Util;
import com.platfos.pongift.security.SHA256Util;
import com.platfos.pongift.store.model.Store;
import com.platfos.pongift.store.service.StoreService;
import com.platfos.pongift.telegram.service.TelegramService;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.wideshot.definition.WideShot;
import com.platfos.pongift.wideshot.model.WideShotResponse;
import com.platfos.pongift.wideshot.model.WideShotSMSRequest;
import com.platfos.pongift.wideshot.service.WideShotService;
import com.platfos.pongift.zxing.service.ZxingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class GiftCardService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCardService.class);

    /**
     * 문자 템플릿 서비스
     **/
    private final SIMTemplateInfoService templateService;
    /**
     * 문자 발송 정보 서비스(DB)
     **/
    private final SIMTransmitInfoService transmitInfoService;
    /**
     * 상품권 발송 정보
     **/
    private final POMDispatchInfoService dispatchInfoService;
    /**
     * 공통 기초 코드 서비스
     **/
    private final SIMCodeService codeService;
    /**
     * 상품권 정책 관리/정보 서비스
     **/
    private final SIMPolicyInfoService policyInfoService;
    /**
     * SMS(WideShot) 서비스
     **/
    private final WideShotService wideShotService;
    /**
     * 바코드(Zxing) 서비스
     **/
    private final ZxingService zxingService;
    /**
     * PhantomJS GhostDriver(Html ScreenShot) 서비스
     **/
    private final GhostDriver ghostDriver;
    /** Http 모듈 **/
//    private final RestTemplate restTemplate;
    /**
     * 텔레그램 서비스
     **/
    private final TelegramService telegramService;
    /**
     * 시스템 정보 서비스
     **/
    private final SIMSystemInfoService systemInfoService;
    private final QuantumService quantumService;
    private final CIMLedgerService ledgerService;
    private final SIMTemplateInfoService templateInfoService;
    private final MIMServiceService mimServiceService;
    private final StoreService storeService;
//    private final SSMDirectSendGiftCardService directSendGiftCardInfoService;

    /**
     * 상품권 변경 유효성 검사
     *
     * @param ledger   상품권 원장
     * @param modifyTp 변경 유형
     * @throws Exception
     */
    public void validate(CIMLedger ledger, ModifyTp modifyTp) throws Exception {
        if (ledger == null) {
            throw new GiftException(ResponseCode.FAIL_GIFT_NULL_POINTER_EXCEPTION);
        }
        //상품권 원장 상태 조회
        SIMCode giftStCode = codeService.getSIMCode(SIMCodeGroup.giftSt, ledger.getGiftSt());

        if (modifyTp == ModifyTp.USE | modifyTp == ModifyTp.RESEND | modifyTp == ModifyTp.CHANGE_NUMBER) { //사용, 재전송, 번호 변경 재전송
            if (GiftSt.findByCode(giftStCode.getCodeCd()) != GiftSt.UNUSED) { //미사용 상태 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_UPDATE_ST, giftStCode.getCodeNm());
            }
            if (System.currentTimeMillis() > Util.strdate2timestamp(ledger.getExpiryDt() + "235959", "yyyyMMddHHmmss")) { //만료 기간 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_EXPIRATION);
            }
        } else if (modifyTp == ModifyTp.USE_CANCEL) { //사용취소
            if (GiftSt.findByCode(giftStCode.getCodeCd()) != GiftSt.USED) { //사용 상태 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_CANCELED_USE, giftStCode.getCodeNm());
            }
            //사용 취소 기간 확인(상품권 정책 관리)
            SIMPolicyInfo policyInfo = policyInfoService.selectDateLessThanOrEqual("withdraw_period", new SimpleDateFormat("yyyyMMddHHmmss").format(ledger.getModDate()), "%Y%m%d%H%i%s", "HOUR");
            if (policyInfo.getResult() == 0) {
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_CANCELED_USE_EXPIRATION, String.valueOf(policyInfo.getValue() + 1));
            }
        }
    }

    /**
     * 상품권 변경 유효성 검사(사용 취소 기간 미체크)
     *
     * @param ledger   상품권 원장
     * @param modifyTp 변경 유형
     * @throws Exception
     */
    public void validateUncheckedPeriod(CIMLedger ledger, ModifyTp modifyTp) throws Exception {
        if (ledger == null) {
            throw new GiftException(ResponseCode.FAIL_GIFT_NULL_POINTER_EXCEPTION);
        }
        //상품권 원장 상태 조회
        SIMCode giftStCode = codeService.getSIMCode(SIMCodeGroup.giftSt, ledger.getGiftSt());

        if (modifyTp == ModifyTp.USE | modifyTp == ModifyTp.RESEND | modifyTp == ModifyTp.CHANGE_NUMBER) { //사용, 재전송, 번호 변경 재전송
            if (GiftSt.findByCode(giftStCode.getCodeCd()) != GiftSt.UNUSED) { //미사용 상태 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_UPDATE_ST, giftStCode.getCodeNm());
            }
            if (System.currentTimeMillis() > Util.strdate2timestamp(ledger.getExpiryDt() + "235959", "yyyyMMddHHmmss")) { //만료 기간 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_EXPIRATION);
            }
        } else if (modifyTp == ModifyTp.USE_CANCEL) { //사용취소
            if (GiftSt.findByCode(giftStCode.getCodeCd()) != GiftSt.USED) { //사용 상태 확인
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_CANCELED_USE, giftStCode.getCodeNm());
            }
        }
    }


    /**
     * 상품권 템플릿 정보 조회
     *
     * @param smsTemplateMap Map
     * @param isAddBytes     문자 이미지 정보 포함 여부
     * @return
     * @throws Exception
     */
    public GiftCardTemplateInfo generateGiftCard(Map<String, String> smsTemplateMap, boolean isAddBytes) throws Exception {
        if (ObjectUtils.isEmpty(smsTemplateMap)) return null;

        //문자 템플릿 정보 조회
        SIMTemplateInfo template = templateService.getSIMTemplateInfo(smsTemplateMap.get("templateId"));
        if (template != null) {
            //템플릿에 데이터 주입
            Set<String> keyset = smsTemplateMap.keySet();
            for (String code : keyset) {
                if (!code.equals("templateId")) {
                    injectionDataInTemplate(template, code, smsTemplateMap.get(code));
                }
            }

            //문자 이미지 생성
            byte[] bytes = null;
            if (isAddBytes) { //문자 이미지 정보 포함 여부 확인
                //상품권 템플릿 유형
                FormTp formTp = FormTp.findByCode(template.getFormTp());

                //CaptureConfig 생성
                CaptureConfig captureConfig = new CaptureConfig();
                //Capture Image 사이즈 설정
                if ((template.getGroundWidth() != null && template.getGroundWidth() > 0) & (template.getGroundHeight() != null && template.getGroundHeight() > 0)) {
                    captureConfig.setWidth(template.getGroundWidth());
                    captureConfig.setHeight(template.getGroundHeight());
                } else {
                    //HTML 형태인지 TEXT 형태인지...
                    boolean isHtmlCd = (formTp == FormTp.SALES | formTp == FormTp.RESEND | formTp == FormTp.CHANGE_NUMBER | formTp == FormTp.GUIDE);
                    String html = isHtmlCd ? template.getHtmlCd() : template.getTextCd();
                    captureConfig.setWidth(html.getBytes().length * 6);
                    captureConfig.setHeight(22);
                }

                //Capture Image Request URL
                String url = "http://127.0.0.1:914/giftcard/preview";

                //Capture Image Request Parameter(QueryString) 생성
                String queryString = smsTemplateMap.entrySet().stream()
                        .map(p -> p.getKey() + "=" + urlEncode(p.getValue()))
                        .reduce((p1, p2) -> p1 + "&" + p2)
                        .map(s -> "?" + s)
                        .orElse("");

                //Get Screenshot Image
                BufferedImage capturedImage = ghostDriver.getScreenshotAsBufferedImage(url + queryString, captureConfig);

                //Draw Image
                Image drawImage = null;
                //Draw Image Size 설정
                int drawWidth = capturedImage.getWidth();
                int drawHeight = capturedImage.getHeight();

                //상품권 문자 여부 판별
                boolean isGiftCardType = (formTp == FormTp.SALES | formTp == FormTp.RESEND | formTp == FormTp.CHANGE_NUMBER);
                //500x500의 이하의 이미지가 MMS 전송 성공률 높음
                int maxSize = 500;

                //이미지 리사이즈 필요 여부 확인(상품권 문자 용도 확인, maxSize 보다 큰지)
                if (isGiftCardType & (capturedImage.getWidth() > maxSize | capturedImage.getHeight() > maxSize)) {
                    //이미지 리사이즈
                    if (drawWidth >= drawHeight) {
                        drawWidth = maxSize;
                        drawHeight = drawWidth * capturedImage.getHeight() / capturedImage.getWidth();
                    } else {
                        drawHeight = maxSize;
                        drawWidth = drawHeight * capturedImage.getWidth() / capturedImage.getHeight();
                    }
                    drawImage = capturedImage.getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH);
                } else {
                    drawImage = capturedImage;
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BufferedImage newImage = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_RGB);

                //JPG Convert
                Graphics2D g = newImage.createGraphics();
                g.setComposite(AlphaComposite.Src);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(drawImage, 0, 0, Color.WHITE, null);
                g.dispose();
                ImageIO.write(newImage, "jpg", byteArrayOutputStream);

                bytes = byteArrayOutputStream.toByteArray();
            }

            //상품권 템플릿 정보 생성
            return GiftCardTemplateInfo.builder().template(template).bytes(bytes).build();
        }
        return null;
    }

    /**
     * 템플릿에 데이터 주입
     *
     * @param template 템플릿 정보
     * @param code     템플릿 코드
     * @param value    템플릿 값
     * @throws IOException
     * @throws WriterException
     * @throws NotFoundException
     */
    private void injectionDataInTemplate(SIMTemplateInfo template, String code, String value) throws IOException, WriterException, NotFoundException {
        if (code.equals("giftBarcode")) { //바코드
            if (!StringUtils.isEmpty(template.getCodeTp())) {
                GiftCardCodeTp barcodeTp = GiftCardCodeTp.findByCode(template.getCodeTp());
                //바코드 생성
                byte[] codeImage = zxingService.generateCodeImage(value, barcodeTp.getFormat(), template.getCodeWidth(), template.getCodeHeight());
                value = "data:image/png;base64," + new String(Base64.getEncoder().encode(codeImage));
            }
        } else if (code.endsWith("Amount")) { //금액 관련 코드
            if (StringUtils.isNumeric(value)) {
                value = new DecimalFormat(",###.##").format(Integer.parseInt(value));
            }
        }
        logger.info("code: {}, value: {}", code, value);
        //Html, Text 템플릿 코드 둘 다 주입
        if (StringUtils.isNotEmpty(template.getTextCd()))
            template.setTextCd(template.getTextCd().replace("[%" + code + "%]", value));
        if (StringUtils.isNotEmpty(template.getHtmlCd()))
            template.setHtmlCd(template.getHtmlCd().replace("[%" + code + "%]", value));

        //알림톡 내용
        if (StringUtils.isNotEmpty(template.getTalkContents()))
            template.setTalkContents(template.getTalkContents().replace("[%" + code + "%]", value));
        //알림톡 부가정보(버튼)
        if (StringUtils.isNotEmpty(template.getTalkAddition()))
            template.setTalkAddition(template.getTalkAddition().replace("[%" + code + "%]", value));
    }

    /**
     * 템플릿 문자 발송(비동기)
     *
     * @param smsTemplate 템플릿 문자 정보
     * @throws Exception
     */
    @Async
    public void asyncSms(SMSTemplate smsTemplate) throws Exception {
        sms(smsTemplate);
    }

    /**
     * 템플릿 문자 발송
     *
     * @param smsTemplate 템플릿 문자 정보
     * @throws Exception
     */
    public void sms(SMSTemplate smsTemplate) throws Exception {
        //템플릿 정보 조회
        smsTemplate.getSmsTemplateMap().put("templateId", smsTemplate.getTemplateId());
        SIMTemplateInfo template = templateService.getSIMTemplateInfo(smsTemplate.getTemplateId());

        //WideShot 요청 정보 생성 [시작]
        String transmitId = transmitInfoService.createTransmitId();
        WideShotSMSRequest wideShotSMSRequest = WideShotSMSRequest.builder()
                .userKey(StringUtils.substring(transmitId, 2))
                .receiverTelNo(smsTemplate.getPhoneNo())
                .advertisementYn("N")
                .build();

        WideShot wideShot = null;

        // 상품권 템플릿 유형
        // FormTp formTp = FormTp.findByCode(template.getFormTp());

        //알림톡 사용 여부
        boolean talkUseSt = false;

        if (StringUtils.isNotEmpty(template.getTalkTemplateCd()) && StringUtils.isNotEmpty(template.getTalkContents())) { //알림톡 기본 정보 확인
            if (smsTemplate.getSmsTemplateMap().containsKey("sendTalkSt")) { //알림톡 전송 여부 확인(파라메터)
                talkUseSt = smsTemplate.getSmsTemplateMap().get("sendTalkSt").equals("Y");
            }
            //알림톡 전송 여부 확인(Database)
            else if (StringUtils.isNotEmpty(template.getTalkUseSt()) && template.getTalkUseSt().equals("Y"))
                talkUseSt = true;
        }

        if (talkUseSt) { //알림톡 전송여부
            wideShot = WideShot.ALIMTALK; //알림톡 설정
            GiftCardTemplateInfo giftCardTemplate = generateGiftCard(smsTemplate.getSmsTemplateMap(), false);
            wideShotSMSRequest.setTemplateCode(giftCardTemplate.getTemplate().getTalkTemplateCd()); // 알림톡 템플릿 코드
            wideShotSMSRequest.setContents(giftCardTemplate.getTemplate().getTalkContents()); // 내용
            wideShotSMSRequest.setAttachment(giftCardTemplate.getTemplate().getTalkAddition()); // attachment(알림톡 부가 정보)
        } else {
            /* 상품권 이미지 제거 요청 (기획팀 요청, 2023/07/13)
            boolean isGiftCardType = (formTp == FormTp.SALES | formTp == FormTp.RESEND | formTp == FormTp.CHANGE_NUMBER);

            if (isGiftCardType) { //MMS
                wideShot = WideShot.MMS;
                GiftCardTemplateInfo giftCardTemplate = generateGiftCard(smsTemplate.getSmsTemplateMap(), true);

                sms.setContents(giftCardTemplate.getTemplate().getTextCd()); // 내용

                //이미지
                List<Map<String, Object>> imageFiles = new ArrayList<>();
                Map<String, Object> file = new HashMap<>();
                file.put("bytes", giftCardTemplate.getBytes());
                file.put("fileName", UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
                imageFiles.add(file);
                sms.setImageFiles(imageFiles);
            } else { //SMS OR LMS
            */

            GiftCardTemplateInfo giftCardTemplate = generateGiftCard(smsTemplate.getSmsTemplateMap(), false);
            String html = giftCardTemplate.getTemplate().getTextCd();

            if (html.getBytes().length > 80) { //80bytes 이상 LMS
                wideShot = WideShot.LMS;
                wideShotSMSRequest.setContents(html); //내용
            } else { //SMS
                wideShot = WideShot.SMS;
                wideShotSMSRequest.setSms(html);
            }
        }

        // WideShot 요청 정보 생성 [끝]

        //WideShot SMS 전송 요청
        WideShotResponse response = wideShotService.send(wideShot, wideShotSMSRequest);

        //문자 전송 요청 정보 생성 및 저장
        SIMTransmitInfo smsResult = SIMTransmitInfo.builder()
                .transmitId(transmitId)
                .dispatchId(smsTemplate.getDispatchId())
                .templateId(smsTemplate.getTemplateId())
                .requestCd(response.getCode())
                .sendTp(wideShot.getSendTp())
                .sendSt(SIMTransmitInfoSendSt.READY.getCode())
                .inquiryCnt(0)
                .build();
        transmitInfoService.insertSIMTransmitInfo(smsResult);
    }

    /**
     * 문자 발송 결과 처리 (비동기)
     */
    @Async
    public void asyncSendResultUpdate() {
        //문자 전송 결과 조회(리스트)
        WideShotResponse wideShotResponse = wideShotService.send(WideShot.RESULTS, null);

        List<Map<String, Object>> datas = (List) wideShotResponse.getData();
        if (!ObjectUtils.isEmpty(datas)) {
            for (Map<String, Object> data : datas) {
                String sendCode = (String) SIMIndex.SIM_TRANSMIT_INFO.getCode() + data.get("sendCode"); // 와이드샷 서버 이전 userKey 길이 조정 관련
                String resultCode = (String) data.get("resultCode");

                if (!StringUtils.isEmpty(sendCode)) {
                    if (sendCode.startsWith(SIMIndex.SIM_TRANSMIT_INFO.getCode()) & sendCode.length() == 14) {
                        //문자 전송 정보 조회
                        SIMTransmitInfo transmitInfo = transmitInfoService.getSIMTransmitInfo(sendCode);

                        //전송 상태 '대기'
                        if (SIMTransmitInfoSendSt.findByCode(transmitInfo.getSendSt()) == SIMTransmitInfoSendSt.READY) {
                            //전송 결과 조회 시도 횟수 설정
                            transmitInfo.setInquiryCnt(transmitInfo.getInquiryCnt() + 1);

                            //알람 전송 여부
                            boolean isNotiAlert = false;

                            //카카오 미사용자 판단 에러코드
                            String[] kakaoErrors = {
                                    "3018", //메시지를 전송할 수 없음 (친구톡의 경우 친구가 아닌 경우 등)
                                    "7110", //카카오 내부시스템 오류로 메시지 전송실패
                                    "7111", //카카오 서버와 연결되어 있지 않는 카카오톡 사용자
                                    "7199"  //카카오 메시지상태 unknown Error
                            };

                            //전송 결과 코드 존재 여부 확인
                            if (!StringUtils.isEmpty(resultCode)) {
                                transmitInfo.setResultCd(resultCode);
                                if (resultCode.equals("100")) { //전송 결과 성공
                                    transmitInfo.setSendSt(SIMTransmitInfoSendSt.SUCCESS.getCode());
                                } else if (ArrayUtils.contains(kakaoErrors, resultCode) | (resultCode.startsWith("8") & resultCode.length() == 4)) {
                                    try {
                                        //알림톡 전송 요청 정보 조회
                                        SIMTransmitForAlimTalk alimTalk = transmitInfoService.getSIMTransmitForAlimTalk(transmitInfo.getTransmitId());
                                        GoodsSupplyInfoForAlimTalk goodsSupplyInfo = transmitInfoService.getGoodsSupplyInfoByAlimTalk(alimTalk);

                                        int giftMaxResendCnt = 3;
                                        int resendCount = (dispatchInfoService.getResendCountByProductId(alimTalk.getProductId()));
                                        SIMPolicyInfo policyInfo = policyInfoService.getSIMPolicyInfo();
                                        String customerCenterTel = systemInfoService.getValue("customer.center.tel");
                                        String giftStampDutyInfo = systemInfoService.getValue("gift.stamp.duty.info");

                                        FormTp formTp;
                                        if (alimTalk.getChannelGb().equals(ChannelGb.PLATFOS.getCode())) {
                                            formTp = TradeTp.findByCode(alimTalk.getTradeTp()) == TradeTp.SEND_DIRECT ? FormTp.SEND_DIRECT : FormTp.SEND_GOODS;
                                        } else {
                                            formTp = FormTp.SALES;
                                        }

                                        Map<String, String> smsTemplateMap = new HashMap<>();
                                        smsTemplateMap.put("giftResendCnt", String.valueOf(resendCount));
                                        smsTemplateMap.put("giftMaxResendCnt", String.valueOf(giftMaxResendCnt));
                                        smsTemplateMap.put("serviceNm", goodsSupplyInfo.getServiceNm());
                                        smsTemplateMap.put("contents", alimTalk.getGoodsNm());
                                        smsTemplateMap.put("goodsNm", alimTalk.getGoodsNm());
                                        smsTemplateMap.put("giftExpiryDt", Util.timestamp2strdate(Util.strdate2timestamp(alimTalk.getExpiryDt(), "yyyyMMdd"), "yy.MM.dd"));
                                        smsTemplateMap.put("purchaseDt", Util.timestamp2strdate(Util.strdate2timestamp(alimTalk.getPurchaseDt(), "yyyyMMddHHmmss"), "yy.MM.dd"));
                                        smsTemplateMap.put("purchaseCnt", "1");
                                        smsTemplateMap.put("purchaseAmount", String.valueOf(alimTalk.getGiftAmt())); // 2023-01-09 소비자가로 변경 요청
                                        smsTemplateMap.put("goodsExchangePlace", goodsSupplyInfo.getExchangePlace());
                                        smsTemplateMap.put("guide", "-");
                                        smsTemplateMap.put("centerTel", goodsSupplyInfo.getCenterTel());
                                        smsTemplateMap.put("buyerPhoneNo", Util.strToPhoneNumber(AES256Util.decode(alimTalk.getBuyerPhone())));
                                        smsTemplateMap.put("receiverPhoneNo", Util.strToPhoneNumber(AES256Util.decode(alimTalk.getReceiverPhone())));
                                        smsTemplateMap.put("publicLegerId", alimTalk.getLedgerId().replace("GL", ""));
                                        smsTemplateMap.put("goodsInfo", goodsSupplyInfo.getGoodsInfo());
                                        smsTemplateMap.put("expireDt", policyInfo.getExpireDt());
                                        smsTemplateMap.put("customerCenterTel", Util.strToPhoneNumber(customerCenterTel));
                                        smsTemplateMap.put("giftStampDutyInfo", giftStampDutyInfo);
                                        smsTemplateMap.put("publicCode", AES256Util.encodeHex(alimTalk.getLedgerId()));
                                        smsTemplateMap.put("giftAmtText", new DecimalFormat(",###.##").format(alimTalk.getGiftAmt()) + "원"); // 2023-01-09 소비자가로 변경 요청
                                        smsTemplateMap.put("giftAmt", String.valueOf(alimTalk.getGiftAmt()));
                                        smsTemplateMap.put("sendTalkSt", "N");

                                        SIMTemplateInfo templateInfo = templateInfoService.getSIMTemplateInfoByFormTp(formTp);
                                        SMSTemplate smsTemplate = SMSTemplate.builder().templateId(templateInfo.getTemplateId()).phoneNo(AES256Util.decode(alimTalk.getReceiverPhone())).dispatchId(alimTalk.getDispatchId()).smsTemplateMap(smsTemplateMap).build();

                                        sms(smsTemplate);
                                    } catch (Exception e) {
                                        logger.error(e.getMessage());
                                    }
                                    transmitInfo.setSendSt(SIMTransmitInfoSendSt.FAIL.getCode());
                                } else { //그외 실패
                                    transmitInfo.setSendSt(SIMTransmitInfoSendSt.FAIL.getCode());
                                }
                            } else { //전송 결과 없음
                                transmitInfo.setResultCd(null);
                                if (transmitInfo.isTimeOver()) { //문자 전송 결과 만료 시간 확인
                                    transmitInfo.setSendSt(SIMTransmitInfoSendSt.FAIL.getCode());
                                }
                            }

                            //문자 전송 정보 결과 갱신
                            transmitInfoService.updateSIMTransmitInfo(transmitInfo);
                        }
                    }
                }
            }
        }
    }

    /**
     * URL Encode
     *
     * @param str
     * @return
     */
    private String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 상품 공급자 상태 확인
     *
     * @param goodsServiceId
     * @throws GiftException
     */
    public void checkGoodsServiceSt(String goodsServiceId) throws GiftException {
        MIMService goodsStoreService = mimServiceService.getMIMService(goodsServiceId);
        if (goodsStoreService == null | (goodsStoreService != null && MIMServiceSt.findByCode(goodsStoreService.getServiceSt()) != MIMServiceSt.ACTIVE)) {
            throw new GiftException(ResponseCode.FAIL_GIFT_SERVICE_ST_NOT_USED);
        }
    }

    public void checkGoodsServiceSt(String goodsServiceId, String storeServiceId) throws GiftException {
        if (!goodsServiceId.equals(storeServiceId)) {
            checkGoodsServiceSt(goodsServiceId);
        }
    }

    /**
     * 상품권 사용처 확인 (본사 서비스 포함)
     *
     * @param ledger
     * @param store
     * @param agencyId
     * @throws GiftException
     */
    public void checkLedgerByUsedStore(CIMLedger ledger, Store store, String agencyId) throws GiftException {
        if (ledger == null) throw new GiftException(ResponseCode.FAIL_GIFT_BARCODE_NOT_EXIST);
        if (!ledger.getServiceId().equals(store.getServiceId())) {
            if (StringUtils.isEmpty(store.getOwnedId())) {
                throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_USED_STORE);
            } else {
                Store ownedStore = storeService.getStore(agencyId, store.getOwnedId());
                if (ownedStore != null) {
                    if (!ledger.getServiceId().equals(ownedStore.getServiceId())) {
                        throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_USED_STORE);
                    }
                } else {
                    throw new GiftException(ResponseCode.FAIL_GIFT_CANNOT_BE_USED_STORE);
                }
            }
        }
    }

    /**
     * 재발송 상품권 조회
     *
     * @param resendGiftCard
     * @return
     * @throws GiftException
     */
    public CIMLedger getLedgerByResendGiftCard(ResendGiftCard resendGiftCard) throws GiftException {
        CIMLedger ledger;
        if (resendGiftCard.getLedgerId() != null) {
            ledger = ledgerService.getCIMLedger(resendGiftCard.getLedgerId());
        } else if (resendGiftCard.getChannelGb() != null) {
            MIMService channelService = mimServiceService.getMIMServiceByChannelGb(resendGiftCard.getChannelGb());
            ledger = ledgerService.getCIMLedgerByTradeList(resendGiftCard.getTradeTp(), resendGiftCard.getOrderNo(), channelService.getServiceId());
        } else {
            throw new GiftException(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER);
        }
        return ledger;
    }
}
