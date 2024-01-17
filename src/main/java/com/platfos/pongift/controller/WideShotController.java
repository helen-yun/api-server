package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.CustomCode;
import com.platfos.pongift.validate.constraints.File;
import com.platfos.pongift.validate.constraints.Number;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.group.*;
import com.platfos.pongift.wideshot.definition.WideShot;
import com.platfos.pongift.wideshot.model.WideShotResultRequest;
import com.platfos.pongift.wideshot.model.WideShotSMSRequest;
import com.platfos.pongift.wideshot.model.WideShotResponse;
import com.platfos.pongift.wideshot.service.WideShotService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@ParamClzz(name = "96. WideShot SMS")
public class WideShotController {
    /** SMS(WideShot) 서비스 **/
    @Autowired
    WideShotService service;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @Validated(GroupA.class)
    @ParamMethodValidate(methodName = "SMS 발송")
    @RequestMapping(path = "wideshot/sms", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> sms(HttpServletRequest request, @RequestBody @Valid WideShotSMSRequest sms) {
        WideShotResponse wideShotResponse = service.send(WideShot.SMS, sms);
        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @Validated(GroupB.class)
    @ParamMethodValidate(methodName = "LMS 발송")
    @RequestMapping(path = "wideshot/lms", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> lms(HttpServletRequest request, @RequestBody @Valid WideShotSMSRequest sms) {
        WideShotResponse wideShotResponse = service.send(WideShot.LMS, sms);
        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @Validated(GroupC.class)
    @ParamMethodValidate(methodName = "MMS 발송(데이터)")
    @RequestMapping(path = "wideshot/mms/data", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> mmsData(HttpServletRequest request, @RequestBody @Valid WideShotSMSRequest sms) {
        List<Map<String, Object>> imageFiles = new ArrayList<>();
        for(String image : sms.getImages()){
            imageFiles.add(service.convertFile(image));
        }
        sms.setImageFiles(imageFiles);

        WideShotResponse wideShotResponse = service.send(WideShot.MMS, sms);
        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "MMS 발송(바이너리)")
    @RequestMapping(path = "wideshot/mms/files", method= RequestMethod.POST)
    public DataResponse<WideShotResponse> mmsFiles(HttpServletRequest request
            , @RequestParam(required = false) @Length(max = 12) @Number @Description("발신번호") String callback
            , @RequestParam @Require @Length(max = 2000) @Description("내용") String contents
            , @RequestParam @Require @Length(max = 12) @Number @Description("수신자 전화번호") String receiverTelNo
            , @RequestParam(required = false) @Description("발송코드 (문자 고유 식별자)") String userKey
            , @RequestParam(required = false) @Description("문자 제목") @Length(max = 100) String title
            , @RequestParam(required = false) @CustomCode(codes = {"Y","N"}) @Description("광고 여부 (기본:N)") String advertisementYn
            , @RequestParam @Require @Size(min = 1, max = 3) @File(maxFileSize = "1MB", maxImageWidth = 1000, maxImageHeight = 1000, supportAttachType = { AttachDataType.BINARY }, supportFileFormat = {"jpg"}) @Description("MMS 이미지 파일") List<MultipartFile> files) throws IOException {
        WideShotSMSRequest sms = WideShotSMSRequest.builder()
                .callback(callback)
                .contents(contents)
                .receiverTelNo(receiverTelNo)
                .userKey(userKey)
                .title(title)
                .build();

        List<Map<String, Object>> imageFiles = new ArrayList<>();
        for(MultipartFile multipartFile : files){
            Map<String, Object> file = new HashMap<>();
            file.put("bytes", multipartFile.getBytes());
            file.put("fileName", multipartFile.getOriginalFilename());
            imageFiles.add(file);
        }
        sms.setImageFiles(imageFiles);

        WideShotResponse wideShotResponse = service.send(WideShot.MMS, sms);

        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }

    /** WideShot 알림톡 발송 API 추가 **/
    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @Validated(GroupE.class)
    @ParamMethodValidate(methodName = "알림톡 발송")
    @RequestMapping(path = "wideshot/alimtalk", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> alimtalk(HttpServletRequest request, @RequestBody @Valid WideShotSMSRequest sms) {
        WideShotResponse wideShotResponse = service.send(WideShot.ALIMTALK, sms);
        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "발송 결과 조회")
    @RequestMapping(path = "wideshot/result", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> result(HttpServletRequest request
            , @RequestParam @Require @Description("발송코드 (문자 고유 식별자)") String sendCode) {
        WideShotResponse wideShotResponse = service.send(WideShot.RESULT, WideShotResultRequest.builder().sendCode(sendCode).build());
        return responseService.data(request, ResponseCode.SUCCESS, wideShotResponse);
    }

    @APIPermission(roles = { APIRole.EXTERNAL_SERVICE })
    @ParamMethodValidate(methodName = "발송 결과 리스트 조회")
    @RequestMapping(path = "wideshot/results", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<WideShotResponse> results(HttpServletRequest request) {
        WideShotResponse wideShotResponse = service.send(WideShot.RESULTS, null);
        return responseService.data(request, (wideShotResponse.isSuccess()?ResponseCode.SUCCESS:ResponseCode.FAIL), wideShotResponse);
    }
}