package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.model.ParamValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@ParamClzz(name = "1. 공통 코드 조회")
public class CodeController {
    /** 기초 코드 서비스(DB) **/
    @Autowired
    SIMCodeService service;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    @APIPermission(roles = { APIRole.AUTHENTICATED })
    @ParamMethodValidate(methodName = "codes")
    @RequestMapping(path = "code/{groupCode}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<SIMCode> codes(HttpServletRequest request, @PathVariable(value="groupCode") String groupCode) throws Exception {
        SIMCodeGroup codeGroup = SIMCodeGroup.valueOf(groupCode);
        return responseService.list(request, service.getSIMCodeList(codeGroup));
    }
    @APIPermission(roles = { APIRole.AUTHENTICATED })
    @ParamMethodValidate(methodName = "codeDetails")
    @RequestMapping(path = "code/{groupCode}/{parentValue}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<SIMCode> codeDetails(HttpServletRequest request, @PathVariable(value="groupCode") String groupCode, @PathVariable(value="parentValue") String parentValue) throws Exception {
        SIMCodeGroup parentCodeGroup = SIMCodeGroup.valueOf(groupCode);
        SIMCodeGroup codeGroup = SIMCodeGroup.findByParentCodeGroup(parentCodeGroup, parentValue);
        if(codeGroup == null){
            return responseService.list(request, ResponseCode.FAIL, null);
        }
        return responseService.list(request, service.getSIMCodeList(codeGroup));
    }
    @APIPermission(roles = { APIRole.AUTHENTICATED })
    @ParamMethodValidate(methodName = "codeDetails2")
    @RequestMapping(path = "code/{groupCode}/{parentValue}/{code}", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public ListResponse<SIMCode> codeDetails2(HttpServletRequest request, @PathVariable(value="groupCode") String groupCode, @PathVariable(value="parentValue") String parentValue, @PathVariable(value="code") String code) throws Exception {
        SIMCodeGroup codeGroup = SIMCodeGroup.valueOf(code);
        return responseService.list(request, service.getSIMCodeList(codeGroup));
    }
}
