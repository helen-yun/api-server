package com.platfos.pongift.telegram.service;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.sim.service.SIMSystemInfoService;
import com.platfos.pongift.definition.SystemKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {
    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);

    @Autowired
    ApplicationProperties properties;

    @Autowired
    private SIMSystemInfoService systemInfoService;

    @Autowired
    RestTemplate restTemplate;

    private boolean isAvailable(){
        return (!StringUtils.isEmpty(systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_BOTID)) & !StringUtils.isEmpty(systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_CHATID)));
    }
    public String sendPhoto(String caption, String fileName, byte [] fileBytes){
        if(!isAvailable()) return null;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("chat_id", Integer.parseInt(systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_CHATID)));
        body.add("caption", caption);
        body.add("photo", new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() throws IllegalStateException {
                return fileName;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange("https://api.telegram.org/bot"+systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_BOTID)+"/sendPhoto",
                HttpMethod.POST, requestEntity, String.class);

        return response.toString();
    }

    public String sendMessage(String text) throws Exception {
        if(!isAvailable()) return null;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        body.add("chat_id", Integer.parseInt(systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_CHATID)));
        body.add("text", text);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.telegram.org/bot"+systemInfoService.getValue(SystemKey.ALARM_TELEGRAM_BOTID)+"/sendMessage",
                HttpMethod.POST, requestEntity, String.class);

        return response.toString();
    }

    @Async
    public void asyncSendMessage(String text){
        String serverName = properties.getServerName();

        if(StringUtils.isEmpty(text)) return;
        text = "["+serverName+"]"+ System.lineSeparator()+text;
        String afterText = null;
        if(text.length() > 4096){
            afterText = text.substring(4095);
            text = text.substring(0, 4096);
        }
        try {
            sendMessage(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        asyncSendMessage(afterText);
    }
}
