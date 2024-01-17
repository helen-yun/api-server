package com.platfos.pongift.gateway.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.gateway.model.*;
import com.platfos.pongift.definition.ResponseCode;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.telegram.service.TelegramService;
import com.platfos.pongift.validate.model.ParamValidate;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 유통채널 Gateway 연동 서비스
 */
@Service
public class GWService {
    private static final Logger logger = LoggerFactory.getLogger(GWService.class);

    /** 시스템 정보 서비스 **/
    @Autowired
    SIMSystemInfoService systemInfoService;

    /** 공통 기초 코드 서비스 **/
    @Autowired
    SIMCodeService codeService;

    /** Http 모듈 **/
    @Autowired
    RestTemplate restTemplate;

    /** 공통 응답 코드 서비스 **/
    @Autowired
    APIResponseService responseService;

    /**
     * Object convert to MultiValueMap (POST용 Parameter 생성)
     * @param params
     * @return
     */
    public static MultiValueMap<String, String> convertParamMap(Object params) {
        ObjectMapper objectMapper = new ObjectMapper();
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(params, new TypeReference<Map<String, String>>() {});
        paramMap.setAll(map);

        return paramMap;
    }

    /**
     * HttpEntity 생성
     * @param request HttpServletRequest
     * @param contentType Http Request Content-Type
     * @param params Parameter Object
     * @param <T> Parameter Generic Type
     * @return
     */
    public <T> HttpEntity<?> createHttpEntity(HttpServletRequest request, MediaType contentType, T params) {
        return createHttpEntity(request.getMethod(), contentType, params);
    }

    /**
     * HttpEntity 생성
     * @param method Http Request Method Type Name(GET/POST/PUT/DELETE)
     * @param contentType Http Request Content-Type
     * @param params Parameter Object
     * @param <T> Parameter Generic Type
     * @return
     */
    public <T> HttpEntity<?> createHttpEntity(String method, MediaType contentType, T params) {
        HttpHeaders headers = new HttpHeaders();
        if(contentType!=null) headers.setContentType(contentType);

        return new HttpEntity<T>(params, headers);
    }

    /**
     * 유통채널 Gateway Http Send
     * @param responseType 응답 객체 타입
     * @param channelGb 유통채널 구분
     * @param url Http Request URL
     * @param method Http Request Method Type Name(GET/POST/PUT/DELETE)
     * @param httpEntity HttpEntity
     * @param <T> 응답 Generic Type
     * @return
     */
    public <T> GWResponseWrapper<T> send(Class<T> responseType, String channelGb, String url, String method, HttpEntity httpEntity){
        //유통채널 기초 코드 정보 조회
        SIMCode simCode = codeService.getSIMCode(SIMCodeGroup.channelGb, channelGb);

        //유통채널 Gateway Host 정보 조회
        String host = systemInfoService.getGatewayHost(channelGb);

        //URL 생성
        if(host.endsWith("/") & url.startsWith("/")) url = host + url.replaceFirst("/", "");
        else if(!host.endsWith("/") & !url.startsWith("/")) url = host + "/" + url;
        else url = host + url;

        logger.info("============================================================================");
        logger.info("{}({}) send", channelGb, simCode.getCodeNm());
        logger.info("URL : {}", url);
        logger.info("Content-Type : {} , Request-Method : {}", httpEntity.getHeaders().getContentType(), method);


        //Parameter 존재 유무 확인
        if(httpEntity.getBody() != null){
            ObjectMapper mapper = new ObjectMapper();
            try {
                logger.info("-----------------------------------------------");
                logger.info("params");
                logger.info("-----------------------------------------------");
                logger.info(mapper.writeValueAsString(httpEntity.getBody()));
                logger.info("-----------------------------------------------");

                //HttpMethod가 GET인 경우 QueryString으로 URL 재생성
                if(HttpMethod.GET.name().equalsIgnoreCase(method)){
                    MultiValueMap<String, String> paramMap = convertParamMap(httpEntity.getBody());
                    Set<String> set = paramMap.keySet();
                    Iterator<String> it = set.iterator();

                    String urlWithParam = url;
                    while(it.hasNext()){
                        String key = it.next();
                        String val = null;
                        List<String> item = paramMap.get(key);
                        if(!ObjectUtils.isEmpty(item)) val = item.get(0);

                        if(!StringUtils.isEmpty(val)){
                            try { val = URLEncoder.encode(val, "UTF-8"); }catch (Exception e){e.printStackTrace();}

                            if(urlWithParam.equals(url)) urlWithParam += "?" + key + "=" + val;
                            else urlWithParam += "&" + key + "=" + val;
                        }
                    }
                    url = urlWithParam;
                }
            } catch (JsonProcessingException e) {
                logger.error("GWService Parameter Json Processing Exception", e);
            }
        }
        try{
            ResponseEntity<String> res = restTemplate.exchange(
                    url,
                    HttpMethod.valueOf(method),
                    httpEntity,
                    String.class);

            logger.info("============================================================================");
            logger.info(res.getStatusCode().toString());
            logger.info(res.getBody());
            logger.info("============================================================================");

            //response code 2xx (성공)
            if(res.getStatusCode().is2xxSuccessful() && (res.getBody() != null)){
                //응답 String으로 응답 객체 생성(Parsing)
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                if(responseType != null){
                    JavaType type = mapper.getTypeFactory().constructParametricType(GWResponseWrapper.class, responseType);

                    try{
                        return mapper.readValue(res.getBody(), type);
                    }catch (Exception e){
                        return new GWResponseWrapper(false, e.getMessage());
                    }
                }else{
                    try{
                        GWResponseWrapper<T> result = mapper.readValue(res.getBody(), GWResponseWrapper.class);
                        if(result == null | (result != null && result.getResponse() == null)){
                            return new GWResponseWrapper(false, "parsing error : not found response object => "+res.getBody());
                        }else{
                            return result;
                        }
                    }catch (Exception e){
                        return new GWResponseWrapper(false, e.getMessage());
                    }
                }
            }else{
                logger.error("GWService Response Data is null");
                return new GWResponseWrapper(false, "Response Data is null");
            }
        }catch (Exception e){
            logger.error(url+":"+method.toLowerCase(), e);
            return new GWResponseWrapper(false, e.getMessage());
        }
    }

    /**
     * 유통채널 Gateway Http Send(HttpServletRequest에서 URL, Method, Content-Type 참조)
     * @param responseType 응답 객체 타입
     * @param channelGb 유통채널 구분
     * @param request HttpServletRequest
     * @param httpEntity HttpEntity
     * @param <T> 응답 Generic Type
     * @return
     */
    public <T> GWResponseWrapper<T> send(Class<T> responseType, String channelGb, HttpServletRequest request, HttpEntity httpEntity){
        return send(responseType, channelGb, makeGWURL(request), request.getMethod(), httpEntity);
    }

    /**
     * 유통채널 Gateway 유효성 검사(HttpServletRequest에서 URL, Method, Content-Type 참조)
     * @param request HttpServletRequest
     * @param channelGb 유통채널 구분
     * @return
     */
    public ParamValidate validate(HttpServletRequest request, String channelGb){
        return validate(makeGWURL(request), request.getMethod(), channelGb);
    }

    /**
     * 유통채널 Gateway 유효성 검사
     * @param url Http Request URL
     * @param method ttp Request Method Type Name(GET/POST/PUT/DELETE)
     * @param channelGb 유통채널 구분
     * @return
     */
    public ParamValidate validate(String url, String method, String channelGb){
        /** validate start **/
        if(StringUtils.isEmpty(url)){
            logger.error("url is empty");
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "url");
        }
        if(StringUtils.isEmpty(method)){
            logger.error("method is empty");
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "method");
        }
        if(StringUtils.isEmpty(channelGb)){
            logger.error("channelGb is empty");
            return ParamValidate.build(ResponseCode.FAIL_MISSING_REQUEST_PARAMETER, "channelGb");
        }
        /** validate end **/

        //해당 요청에 대한 지원 여부 확인(시스템 정보)
        if(!systemInfoService.existGateway(channelGb, url, method)){
            logger.error("existGateway error : {}", channelGb + "," + url + "," + method);
            return ParamValidate.build(ResponseCode.FAIL_ILLEGAL_REQUEST_PARAMETER, "channelGb", channelGb, "The channel's gateway servlet is not ready. : ["+(url+":"+method.toLowerCase())+"]");
        }

        //유효성 검사 OK
        return ParamValidate.buildSuccess();
    }

    private String makeGWURL(HttpServletRequest request){
        String url = request.getServletPath();
        url = url.split("/gateway/")[1];
        return url;
    }
}
