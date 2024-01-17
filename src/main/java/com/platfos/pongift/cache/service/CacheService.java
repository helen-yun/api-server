package com.platfos.pongift.cache.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.telegram.service.TelegramService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** 캐시 서비스 **/
@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TelegramService telegramService;

    @Async
    /**
     * 모든 API 서버의 캐시 제거(비동기)
     */
    public void asyncCallOtherServerRefreshCache(String servletPath){
        callOtherServerRefreshCache(servletPath);
    }

    /**
     * 모든 API 서버의 캐시 제거
     */
    private void callOtherServerRefreshCache(String servletPath){
        if(!ObjectUtils.isEmpty(properties.getIps())){
            for(String ip : properties.getIps()){
                if(!properties.getServerIp().equals(ip)){
                    if(!servletPath.startsWith("/")) servletPath = "/" + servletPath;

                    call(properties.getServerProtocol() + "://" + ip + ":" + properties.getServerPort() + servletPath +"/me");
                }
            }
        }
    }

    /**
     * http call(GET)
     * @param url
     */
    private void call(String url){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);

        logger.info("============================================================================");
        logger.info("URL : "+url);
        logger.info("============================================================================");
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            logger.info(response.getBody());

        }catch (Exception e){
            logger.error("other server call error", e);
            telegramService.asyncSendMessage(url + System.lineSeparator() + e.getMessage());
        }
        logger.info("============================================================================");
    }
}
