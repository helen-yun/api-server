package com.platfos.pongift.authorization.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.definition.AttributeKey;
import com.platfos.pongift.authorization.wrapper.RequestWrapper;
import com.platfos.pongift.authorization.exception.APIPermissionAuthException;
import com.platfos.pongift.authorization.service.APIPermissionAuthService;
import com.platfos.pongift.authorization.wrapper.ResponseWrapper;
import com.platfos.pongift.definition.AuthorizationHeaderKey;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.validate.exception.ParamValidateException;
import com.platfos.pongift.validate.model.ParamValidate;
import com.platfos.pongift.validate.service.ParamService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * API 권한 Interceptor
 */
@Component
public class APIPermissionAuthInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(APIPermissionAuthInterceptor.class);

    /** Application Properties **/
    @Autowired
    ApplicationProperties properties;

    /** API 권한 서비스 **/
    @Autowired
    APIPermissionAuthService service;

    /** Request Parameter 서비스(Validate Check) **/
    @Autowired
    ParamService paramService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        if(servletPath.equals("/") | servletPath.equals("/favicon.ico")) return true;

        String reqContentType = (StringUtils.isEmpty(request.getContentType()))?"":(" ["+request.getContentType()+"]");
        String ip = request.getHeader("X-FORWARDED-FOR");
        if(StringUtils.isEmpty(ip)) ip = request.getRemoteAddr();
        //set Request IP ADDRESS
        request.setAttribute(AttributeKey.IP_ADDRESS_KEY.getKey(), ip);

        long datetime = System.currentTimeMillis();

        //set Request ClientIP (For Response log)
        request.setAttribute("clientIP", ip);
        //set Request Start Time (For Response log)
        request.setAttribute("startTime", datetime);
        //set Request Params
        request.setAttribute(AttributeKey.DATE_TIME_KEY.getKey(), datetime);

        //Write Request Log
        logger.info(ip+reqContentType+" ("+ servletPath +":"+request.getMethod()+") ====================================");

        //server IP
        String serverIp = localIP();
        serverIp = serverIp.substring(0, serverIp.lastIndexOf("."));

        //Request Header ServiceId
        String serviceId = request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey());
        //Request Parameter
        String param = requestBody(request);

        //set Request Params
        request.setAttribute(AttributeKey.PARAMS_KEY.getKey(), param);

        //민감성 정보 필터
        String logParam = Util.logJsonSecretFilter(param, "giftBarcode", "[SECRET VALUE]");
        //param log write
        logger.info("==> "+logParam);

        if(handler instanceof HandlerMethod){
            //접속 Class(Controller)
            Class<?> clzz = ((HandlerMethod) handler).getBeanType();
            //접속 Method(in Controller)
            Method method = ((HandlerMethod) handler).getMethod();

            //API 권한 확인(IP)
            boolean hasPermission = service.permission(request, method, serviceId, ip, serverIp);
            if(!hasPermission) throw new APIPermissionAuthException("No API Permission Authorization : "+ip);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub
        String servletPath = request.getServletPath();
        if(servletPath.equals("/") | servletPath.equals("/favicon.ico")) return;

        String resContentType = (StringUtils.isEmpty(response.getContentType()))?"":(" ["+response.getContentType()+"]");
        String ip = (String) request.getAttribute("clientIP");
        long startTime = (long) request.getAttribute("startTime");

        if(handler instanceof HandlerMethod){
            if(servletPath.equals("/docsjson") | servletPath.equals("/responsejson") | servletPath.equals("/codejson")){
                logger.info("<== PRINT SKIP OUTPUT");
            }else{
                try {
                    ResponseWrapper rw = (ResponseWrapper) response;
                    String output = rw.getOutputString();
                    logger.info("<== "+output);

                }catch (Exception e){e.printStackTrace();}
            }
        }

        //Write Response Log
        logger.info(ip+resContentType+" ("+ servletPath +":"+request.getMethod()+") "+(System.currentTimeMillis() - startTime)+"ms ===============================");
        super.afterCompletion(request, response, handler, ex);
    }

    public String localIP(){
        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
            String ip = local.getHostAddress();
            return ip;
        } catch (UnknownHostException e1) { e1.printStackTrace(); }
        return null;
    }

    public String requestBody(HttpServletRequest request) throws Exception{
        if(request instanceof StandardMultipartHttpServletRequest){
            try {
                Map<String, List<String>> map = new HashMap<>();
                for(Part part : request.getParts()){
                    List<String> files = null;
                    if(map.containsKey(part.getName())) files = map.get(part.getName());
                    else files = new ArrayList<>();

                    String file = part.getSubmittedFileName() + " (" + part.getContentType() + ", " + part.getSize() + "bytes)";
                    files.add(file);

                    map.put(part.getName(), files);
                }
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(map);
            } catch (Exception e) {e.printStackTrace();}
        }else{
            return ((RequestWrapper) request).getInputString();
        }
        return "";
    }
}