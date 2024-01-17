package com.platfos.pongift.controller;

import com.platfos.pongift.attach.model.AttachUploadFile;
import com.platfos.pongift.attach.model.UploadFile;
import com.platfos.pongift.attach.service.AttachFileService;
import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.definition.MethodRoleType;
import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.definition.ResponseCode;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.File;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupA;
import com.platfos.pongift.validate.group.GroupB;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * API Document 생성용(작동 안함)
 * 이미지 서버 API Document
 */
@ExternalController
@Validated
@ParamClzz(name = "97. 파일업로드(이미지서버)")
public class UploadController {
    @Autowired
    APIResponseService responseService;

    @Autowired
    AttachFileService attachFileService;

    @APIPermission(roles = { APIRole.ANONYMOUS })
    @ParamMethodValidate(methodName = "파일 업로드 (바이너리)")
    @RequestMapping(path="attach/file", method = RequestMethod.POST)
    public DataResponse<UploadFile> file(HttpServletRequest request
            , @RequestParam(required = false) @SimIndex(simIndex = SIMIndex.MIM_SERVICE) @Description("서비스ID") String serviceId
            , @RequestParam @Require @File @Description("파일") MultipartFile file) {
        UploadFile uploadFile = null;
        try{ uploadFile = attachFileService.upload(serviceId, file); }catch (Exception e){e.printStackTrace();}

        return responseService.data(request, ((uploadFile != null && uploadFile.isSuccess())?ResponseCode.SUCCESS:ResponseCode.FAIL), null, uploadFile);
    }

    @APIPermission(roles = { APIRole.ANONYMOUS })
    @ParamMethodValidate(methodName = "다중 파일 업로드 (바이너리)")
    @RequestMapping(path="attach/files", method = RequestMethod.POST)
    public ListResponse<UploadFile> files(HttpServletRequest request
            , @RequestParam(required = false) @SimIndex(simIndex = SIMIndex.MIM_SERVICE) @Description("서비스ID") String serviceId
            , @RequestParam @Require @File @Description("파일 리스트") List<MultipartFile> files) {
        List<UploadFile> uploadFiles = new ArrayList<>();
        try{ uploadFiles = attachFileService.uploads(serviceId, files); }catch (Exception e){e.printStackTrace();}

        boolean isSuccess = true;
        if(ObjectUtils.isEmpty(files)){
            isSuccess = false;
        }else if(uploadFiles.size() == files.size()){
            for(UploadFile uploadFile:uploadFiles){
                if(isSuccess & !uploadFile.isSuccess()) isSuccess = false;
            }
        }
        return responseService.list(request, (isSuccess?ResponseCode.SUCCESS:ResponseCode.FAIL), null, uploadFiles);
    }

    @APIPermission(roles = { APIRole.ANONYMOUS })
    @Validated(GroupA.class)
    @ParamMethodValidate(methodName = "파일 업로드 (데이터)")
    @RequestMapping(path="attach/data", method= RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<UploadFile> data(HttpServletRequest request, @RequestBody @Valid AttachUploadFile attachUploadFile) {
        UploadFile uploadFile = null;
        try{ uploadFile = attachFileService.upload(attachUploadFile); }catch (Exception e){e.printStackTrace();}
        return responseService.data(request, ((uploadFile != null && uploadFile.isSuccess())?ResponseCode.SUCCESS:ResponseCode.FAIL), null, uploadFile);
    }

    @APIPermission(roles = { APIRole.ANONYMOUS })
    @Validated(GroupB.class)
    @ParamMethodValidate(methodName = "다중 파일 업로드 (데이터)")
    @RequestMapping(path="attach/datas", method = RequestMethod.POST)
    public ListResponse<UploadFile> datas(HttpServletRequest request, @RequestBody @Valid AttachUploadFile attachUploadFile) {
        List<UploadFile> uploadFiles = new ArrayList<>();
        List<AttachUploadFile> attachUploadFiles = new ArrayList<>();
        for(String data : attachUploadFile.getDatas()){
            attachUploadFiles.add(AttachUploadFile.builder().serviceId(attachUploadFile.getServiceId()).data(data).build());
        }

        try{
            uploadFiles = attachFileService.uploads(attachUploadFiles);
        }catch (Exception e){e.printStackTrace();}

        boolean isSuccess = true;
        if(ObjectUtils.isEmpty(attachUploadFiles)){
            isSuccess = false;
        }else if(uploadFiles.size() == attachUploadFiles.size()){
            for(UploadFile uploadFile:uploadFiles){
                if(isSuccess & !uploadFile.isSuccess()) isSuccess = false;
            }
        }

        return responseService.list(request, (isSuccess?ResponseCode.SUCCESS:ResponseCode.FAIL), null, uploadFiles);
    }
}
