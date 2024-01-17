package com.platfos.pongift.wideshot.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.config.properties.WideshotSystemInfo;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.wideshot.definition.WideShot;
import com.platfos.pongift.wideshot.model.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WideShotService {
    private static final Logger logger = LoggerFactory.getLogger(WideShotService.class);

    @Autowired
    ApplicationProperties properties;

    @Autowired
    SIMSystemInfoService systemInfoService;

    @Autowired
    RestTemplate wideshotRestTemplate;

    @Autowired
    SIMCodeService simCodeService;

    public boolean isReady(){
        WideshotSystemInfo info = systemInfoService.getWideShotInfo();
        return (!StringUtils.isEmpty(info.getHost()) & !StringUtils.isEmpty(info.getKey()));
    }

    public WideShotResponse send(WideShot wideShot, WideShotRequest params){
        if(!isReady()){
            return WideShotResponse.builder().code("P997").message("WideShot Service is not ready.").build();
        }
        WideshotSystemInfo info = systemInfoService.getWideShotInfo();

        ObjectMapper mapper = new ObjectMapper();

        String url = wideShot.getUrl();
        if(info.getHost().endsWith("/") & url.startsWith("/")) url = info.getHost() + url.replaceFirst("/", "");
        else if(!info.getHost().endsWith("/") & !url.startsWith("/")) url = info.getHost() + "/" + url;
        else url = info.getHost() + url;

        logger.info("============================================================================");
        logger.info("URL : {}", url);
        logger.info("Content-Type : {} , Request-Method : {}", wideShot.getContentType(), wideShot.getMethod());

        MultiValueMap paramMap = null;
        if(params != null){
            Map<String, Object> paramInfo = new HashMap<>();
            paramInfo.put("wideShotType", wideShot);
            paramInfo.put("callback", info.getCallback());
            paramInfo.put("userKey", createFileName().substring(0, 12));

            if(wideShot == WideShot.ALIMTALK){
                paramInfo.put("plusFriendId", info.getPlusFriendId());
                paramInfo.put("senderKey", info.getSenderKey());
            }

            paramMap = params.getParams(paramInfo);
            logger.info("-----------------------------------------------");
            logger.info("params");
            logger.info("-----------------------------------------------");
            Set<String> keyset = paramMap.keySet();
            Iterator<String> it = keyset.iterator();
            while(it.hasNext()){
                String key = it.next();
                Object val = paramMap.get(key);
                if(val != null && val instanceof LinkedList){
                    LinkedList linkedList = (LinkedList) paramMap.get(key);
                    if(linkedList != null && linkedList.size() > 0){
                        val = linkedList.get(0);
                    }
                }

                if(val != null){
                    if(val instanceof ByteArrayResource){
                        logger.info(key + " : " + ((ByteArrayResource) val).getFilename());
                    }else{
                        logger.info(key + " : " + val);
                    }
                }else{
                    logger.info(key + " : null");
                }
            }
            logger.info("-----------------------------------------------");

            if(HttpMethod.GET.name().equalsIgnoreCase(wideShot.getMethod())){
                UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).queryParams(paramMap).build();
                url = uriComponents.toUriString();
            }
        }

        ResponseEntity<Map> response = null;

        try{
            if(HttpMethod.GET.name().equalsIgnoreCase(wideShot.getMethod())){
//                wideshotRestTemplate.getInterceptors().add((request, body, execution) -> {
//                    HttpHeaders headers = request.getHeaders();
//                    headers.set("sejongApiKey", info.getKey());
//                    if(wideShot.getContentType() != null) headers.setContentType(wideShot.getContentType());
//                    return execution.execute(request, body);
//                });
//                response = wideshotRestTemplate.getForEntity(url, Map.class);

                HttpHeaders headers = new HttpHeaders();
                headers.add("sejongApiKey", info.getKey());
                if(wideShot.getContentType() != null) headers.setContentType(wideShot.getContentType());
                HttpEntity<MultiValueMap> request = new HttpEntity<>(paramMap, headers);
                response = wideshotRestTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        Map.class);
            }else{
                HttpHeaders headers = new HttpHeaders();
                headers.add("sejongApiKey", info.getKey());
                if(wideShot.getContentType() != null) headers.setContentType(wideShot.getContentType());
                HttpEntity<MultiValueMap> request = new HttpEntity<>(paramMap, headers);

                response = wideshotRestTemplate.exchange(
                        url,
                        HttpMethod.valueOf(wideShot.getMethod()),
                        request,
                        Map.class);
            }

            if(response.getStatusCode().is2xxSuccessful() && (response.getBody() != null && response.getBody() instanceof Map)){
                logger.info("============================================================================");
                logger.info(mapper.writeValueAsString(response.getBody()));
                logger.info("============================================================================");

                WideShotResponse wideShotResponse = new WideShotResponse();
                Map<String, Object> body = response.getBody();
                Set<String> keyset = body.keySet();
                Iterator<String> it = keyset.iterator();
                while(it.hasNext()){
                    String key = it.next();
                    Object val = body.get(key);

                    if(key.equals("code")) wideShotResponse.setCode((String) val);
                    else if(key.equals("message")) wideShotResponse.setMessage((String) val);
                    else if(key.equals("data")) {
                        wideShotResponse.setData(val);
                    }
                }
                return wideShotResponse;
            }else{
                String statusCode = mapper.writeValueAsString(response.getStatusCode());
                String body = mapper.writeValueAsString(response.getBody());
                logger.error("============================================================================");
                logger.error(statusCode);
                logger.error(body);
                logger.error("============================================================================");
                return WideShotResponse.builder().code("P998").message(statusCode + System.lineSeparator() + body).build();
            }
        }catch (Exception e){
            logger.error(wideShot.name(), e);
            return WideShotResponse.builder().code("P999").message(e.getMessage()).build();
        }
    }

    private String createFileName(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    public Map<String, Object> convertFile(String data){
        byte[] bytes = null;
        String extension = null;
        String fileName = null;

        URL url = null;
        try{ url = new URL(data); }catch (MalformedURLException e){e.printStackTrace();}

        if(url == null){
            Pattern p = Pattern.compile("data:(\\S+);base64,");
            Matcher m = p.matcher(data);

            String strMimeType = null;
            if (m.find()) {
                strMimeType = m.group(1);
                strMimeType = properties.getExtension(strMimeType);
            }
            SIMCode mimeType = simCodeService.getSIMCodeBuCodeNm(SIMCodeGroup.mimeType, strMimeType.toLowerCase());

            if(mimeType != null){
                extension = mimeType.getCodeNm().toLowerCase();
                if(extension.equalsIgnoreCase("jpeg")) extension = "jpg";

                if(data.indexOf(";base64,") > -1) {
                    data = data.split(";base64,")[1];
                }
                bytes = Base64.decodeBase64(data);
                fileName = createFileName() + "." + extension;
            }
        }else{
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<String> entity = new HttpEntity<String>(headers);

            ResponseEntity<byte[]> restResp = null;
            try{
                restResp = wideshotRestTemplate.exchange(data, HttpMethod.GET, entity, byte[].class);
            }catch (Exception e){e.printStackTrace();}

            int dotLastIndex = data.lastIndexOf(".");

            String strMimeType = null;
            MediaType contentType = restResp.getHeaders().getContentType();
            if(contentType != null){
                strMimeType = contentType.getSubtype().toLowerCase();
            }else{
                strMimeType = data.substring(dotLastIndex + 1).toLowerCase();
            }

            SIMCode mimeType = simCodeService.getSIMCodeBuCodeNm(SIMCodeGroup.mimeType, strMimeType.toLowerCase());
            if(mimeType != null){
                extension = mimeType.getCodeNm().toLowerCase();
                if(extension.equalsIgnoreCase("jpeg")) extension = "jpg";

                fileName = createFileName() + "." + extension;
                bytes = restResp.getBody();
            }
        }
        if(!ObjectUtils.isEmpty(bytes)){
            Map<String, Object> file = new HashMap<>();
            file.put("bytes", bytes);
            file.put("fileName", fileName);
            return file;
        }
        return null;
    }
}
