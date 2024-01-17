package com.platfos.pongift.attach.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.attach.model.*;
import com.platfos.pongift.data.gim.model.GIMGoods;
import com.platfos.pongift.data.msm.model.MSMStore;
import com.platfos.pongift.definition.SIMAttach;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.data.sim.model.SIMAttachFile;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMAttachFileService;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.definition.SystemKey;
import com.platfos.pongift.response.model.DataResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 첨부 파일 서비스
 */
@Service
public class AttachFileService {
    /** 첨부 파일 관리/정보 서비스(DB) **/
    @Autowired
    SIMAttachFileService simAttachFileService;
    /** 기초 코드 서비스(DB) **/
    @Autowired
    SIMCodeService simCodeService;
    /** 시스템 공통 정보 서비스(DB) **/
    @Autowired
    SIMSystemInfoService systemInfoService;
    /** Http 요청 모듈 **/
    @Autowired
    RestTemplate restTemplate;

    /**
     * 객체 참조 파일 업로드
     * @param obj 지원 객체(GIMGoods, MSMStore)
     * @throws Exception
     */
    public void uploadAttachFileByData(Object obj) throws Exception {
        if(obj instanceof GIMGoods){ //상품 객체
            GIMGoods goods = (GIMGoods) obj;

            //상품 대표 이미지
            uploadAttachFile(goods.getServiceId(), SIMAttach.GOODS, goods.getGoodsId(), goods.getGoodsImage());
            //상품 상세 이미지
            uploadAttachFile(goods.getServiceId(), SIMAttach.GOODS_DETAIL, goods.getGoodsId(), goods.getGoodsDetailImage());
        }else if(obj instanceof MSMStore){ //매장 객체
            MSMStore store = (MSMStore) obj;

            //매장 이미지 1
            uploadAttachFile(store.getServiceId(), SIMAttach.STORE_01, store.getStoreId(), store.getStoreImage1());
            //매장 이미지 2
            uploadAttachFile(store.getServiceId(), SIMAttach.STORE_02, store.getStoreId(), store.getStoreImage2());
            //매장 이미지 3
            uploadAttachFile(store.getServiceId(), SIMAttach.STORE_03, store.getStoreId(), store.getStoreImage3());
        }
    }

    /**
     * 첨부 파일 업로드 및 정보 관리
     * @param serviceId 서비스 아이디
     * @param simAttach 첨부 파일 관리/파일 구분
     * @param linkedId 연동ID (관리구분에 따른 FK)
     * @param attachData 첨부 파일 데이터
     * @throws Exception
     */
    private void uploadAttachFile(String serviceId, SIMAttach simAttach, String linkedId, String attachData) throws Exception {
        //첨부 파일 관리/파일 구분 & 연동ID로 DB 조회
        SIMAttachFile dbAttach = simAttachFileService.getSIMAttachFile(simAttach, linkedId);

        if(StringUtils.isEmpty(attachData)){ //첨부 파일 데이터가 없다면, DB 데이터 삭제 수행
            if(dbAttach != null) { //DB에 데이터가 있는지 확인, 있다면 DB 데이터 삭제 수행
                simAttachFileService.deleteSIMAttachFile(dbAttach.getAttachId());
            }
        }else{ //첨부 파일 데이터가 있다면, DB 데이터 생성/수정 수행
            //파일 업로드
            UploadFile uploadFile = upload(AttachUploadFile.builder().serviceId(serviceId).data(attachData).build());

            if(uploadFile.isSuccess()){ //파일 업로드 성공
                //저장된 파일 전체 경로(URL)
                String url = uploadFile.getUrl();
                //저장된 파일명
                String storeNm = url.substring(url.lastIndexOf('/') + 1);
                //저장된 파일 경로(파일명 제외)
                String path = url.substring(0, url.lastIndexOf('/') + 1);
                //저장된 파일 확장자
                String extension = storeNm.substring(storeNm.lastIndexOf(".") + 1).toLowerCase();
                //저장된 파일형
                SIMCode mimeType = simCodeService.getSIMCodeBuCodeNm(SIMCodeGroup.mimeType, extension.toLowerCase());

                //첨부파일 정보 생성
                SIMAttachFile newAttach = SIMAttachFile.builder()
                        .maintainGb(simAttach.getMaintainGb())
                        .linkedId(linkedId)
                        .fileGb(simAttach.getFileGb())
                        .filePath(path)
                        .storeNm(storeNm)
                        .originNm(uploadFile.getFileName())
                        .fileKind(mimeType.getCodeCd()).build();

                if(dbAttach == null){ //파일 생성
                    //첨부 파일 정보 INSERT
                    simAttachFileService.insertSIMAttachFile(newAttach);
                }else{ //파일 업데이트
                    //첨부파일 ID 설정
                    newAttach.setAttachId(dbAttach.getAttachId());
                    //첨부 파일 정보 UPDATE
                    simAttachFileService.updateSIMAttachFile(newAttach);
                }
            }
        }
    }

    /**
     * Upload Request to CDN Server (이미지 서버 파일 업로드)
     * @param serviceId 서비스 아이디
     * @param file MultipartFile 객체
     * @return
     * @throws Exception
     */
    public UploadFile upload(String serviceId, MultipartFile file) throws Exception{
        //원본 파일명
        String oriFileName = (StringUtils.isEmpty(file.getOriginalFilename()))?null:file.getOriginalFilename();

        //Request Parameters 생성
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("serviceId", serviceId);
        map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), oriFileName));

        //업로드
        return upload("/attach/file", MediaType.MULTIPART_FORM_DATA, map);
    }

    /**
     * Upload Request to CDN Server (이미지 서버 파일 업로드)
     * @param attachUploadFile AttachUploadFile 객체
     * @return
     */
    public UploadFile upload(AttachUploadFile attachUploadFile){
        //업로드
        return upload("/attach/data", MediaType.APPLICATION_JSON, attachUploadFile);
    }

    /**
     * Upload Request to CDN Server (이미지 서버 파일 업로드) : 지정 파일 경로에 업로드
     * @param fileName 지정 파일 경로
     * @param mimeType 파일형태
     * @param bytes 파일 데이터(바이너리)
     * @return
     */
    public UploadFile uploadKeepOriginalFile(String fileName, String mimeType, byte[] bytes){
        //File Date Base64 Encoded
        String data = "data:"+mimeType+";base64,"+Base64.encodeBase64String(bytes);
        //Request AttachUploadFile 객체 생성
        AttachUploadFile attachUploadFile = AttachUploadFile.builder().data(data).fileName(fileName).build();
        //업로드
        return upload("/attach", MediaType.APPLICATION_JSON, attachUploadFile);
    }

    /**
     * Upload Request to CDN Server (이미지 서버 다중 파일 업로드)
     * @param serviceId 서비스 아이디
     * @param files 파일 정보 리스트(MultipartFile)
     * @return
     * @throws Exception
     */
    public List<UploadFile> uploads(String serviceId, List<MultipartFile> files) throws Exception {
        ArrayList<UploadFile> list = new ArrayList<>();
        if(files != null && files.size() > 0) {
            for(MultipartFile file : files) list.add(upload(serviceId, file));
        }
        return list;
    }

    /**
     * Upload Request to CDN Server (이미지 서버 다중 파일 업로드)
     * @param attachUploadFiles 파일 정보 리스트(AttachUploadFile)
     * @return
     * @throws Exception
     */
    public List<UploadFile> uploads(List<AttachUploadFile> attachUploadFiles) throws Exception {
        ArrayList<UploadFile> list = new ArrayList<>();
        if(attachUploadFiles != null && attachUploadFiles.size() > 0) {
            for(AttachUploadFile file : attachUploadFiles) list.add(upload(file));
        }
        return list;
    }

    /**
     * Upload Request to CDN Server (이미지 서버 파일 업로드)
     * @param url CDN Server Request URL
     * @param contentType CDN Server Request ContentType
     * @param params CDN Server Request Parameters
     * @param <T>
     * @return
     */
    private <T> UploadFile upload(String url, MediaType contentType, T params){
        //Request Header Create
        HttpHeaders headers = new HttpHeaders();
        if(contentType!=null) headers.setContentType(contentType); //set Content-Type
        HttpEntity<T> httpEntity = new HttpEntity<T>(params, headers); //set Request Params

        //Send to CDN Server And receive Response
        String response = restTemplate.postForObject(systemInfoService.getValue(SystemKey.UPLOAD_SERVER_HOST)+url, httpEntity, String.class);

        //Response Parsing
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaType type = mapper.getTypeFactory().constructParametricType(DataResponse.class, UploadFile.class);

        try{
            //응답 객체 맵핑
            DataResponse<UploadFile> restResp = mapper.readValue(response, type);
            return restResp.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        //실패 응답 생성 및 반환
        return UploadFile.builder().isSuccess(false).build();
    }
}
