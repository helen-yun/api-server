package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.service.APIPermissionAuthService;
import com.platfos.pongift.data.sim.model.SIMReplyCode;
import com.platfos.pongift.data.sim.service.SIMReplyCodeService;
import com.platfos.pongift.definition.*;
import com.platfos.pongift.document.model.DocCode;
import com.platfos.pongift.document.service.DocumentService;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.model.ListResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.*;
import com.platfos.pongift.validate.constraints.CustomCode;
import com.platfos.pongift.validate.model.ParamClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RestController
@ParamClzz(name = "99. API Document")
public class DocumentController {
    /** API 권한 서비스 **/
    @Autowired
    APIPermissionAuthService permissionAuthService;

    /** 공통 응답 코드 서비스(DB) **/
    @Autowired
    SIMReplyCodeService simReplyCodeService;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    /** API Document 서비스 **/
    @Autowired
    DocumentService documentService;


    @ParamMethodValidate(methodName = "Document JSON")
    @RequestMapping(path = "docsjson", method= RequestMethod.GET)
    public ListResponse<ParamClass> docsjson(
            HttpServletRequest request,
            @RequestParam(required = false) @CustomCode(codes = {
                    "GIFT", "PRODUCT", "CHANNEL", "AGENCY", "PAYMENT", "MESSAGE", "B2B", "PLATFOS", "GATEWAY-CHANNEL"
            }) String apiRole) throws Exception {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if(StringUtils.isEmpty(ip)) ip = request.getRemoteAddr();

        String serverIp = localIP();
        serverIp = serverIp.substring(0, serverIp.lastIndexOf("."));

        //권한 확인
        APIRole role = APIRole.findByAlias(apiRole);
        if(role != null){
            if(role == APIRole.PRODUCT_SUPPLY_AGENCY){ //상품 공급 대행사
                return responseService.list(request, documentService.paramClasses(role));
            }else if(role == APIRole.EXTERNAL_SERVICE){ //내부 서버
                return responseService.list(request, documentService.paramClasses(role));
            }else{
                if(permissionAuthService.permission(ip, serverIp, role)){ //기타 권한
                    return responseService.list(request, documentService.paramClasses(role));
                }
            }
        }
        //내부 서버 권한 확인
        if(permissionAuthService.permission(ip, serverIp, APIRole.EXTERNAL_SERVICE)){
            return responseService.list(request, documentService.paramClasses(role));
        }

        //권한 없음
        return responseService.list(request, ResponseCode.FAIL_NO_AUTH, "No API Permission Authorization : docsjson => accessIp : "+ip+", APIRole : "+apiRole, null);
    }

    @ParamMethodValidate(methodName = "공통 응답 JSON")
    @RequestMapping(path = "responsejson", method= RequestMethod.GET)
    public ListResponse<SIMReplyCode> responsejson(HttpServletRequest request) throws Exception {
        //공통 응답 코드 데이터
        return responseService.list(request, simReplyCodeService.getSIMReplyCodeList(SIMReplyCodeAPIGB.API));
    }

    @ParamMethodValidate(methodName = "공통 코드 JSON")
    @RequestMapping(path = "codejson", method= RequestMethod.GET)
    public DataResponse<Map<String, DocCode>> codejson(HttpServletRequest request) throws Exception {
        //공통 기초 코드 데이터
        return responseService.data(request, documentService.docCodeMap());
    }

    @ParamMethodValidate(methodName = "Document HTML (권한 지정)")
    @RequestMapping(path = "docs/{apiRole}", method= RequestMethod.GET, produces= MediaType.TEXT_HTML_VALUE)
    public String docs(HttpServletRequest request, @PathVariable(value="apiRole", required = false) String apiRole) throws Exception {
        //API Document HTML
        return documentService.createHtml(apiRole);
    }

    @ParamMethodValidate(methodName = "Document HTML")
    @RequestMapping(path = "docs", method= RequestMethod.GET, produces= MediaType.TEXT_HTML_VALUE)
    public String docsIndex(HttpServletRequest request) throws Exception {
        //API Document HTML
        return documentService.createHtml(null);
    }

    private String localIP(){
        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
            String ip = local.getHostAddress();
            return ip;
        } catch (UnknownHostException e1) { e1.printStackTrace(); }
        return null;
    }
}
