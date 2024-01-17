package com.platfos.pongift.config;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.http.HttpComponentsClientHttpRequestWithBodyFactory;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Configuration
public class RestTemplateConfig {
    private final ApplicationProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestWithBodyFactory();
        httpRequestFactory.setConnectTimeout(properties.getRestTemplateConnectionTimeout());
        httpRequestFactory.setReadTimeout(properties.getRestTemplateReadTimeout());
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(properties.getRestTemplateMaxConnectionCount())
                .setMaxConnPerRoute(properties.getRestTemplateMaxConnectionPerRoute()) // connection pool 적용
                .build();
        httpRequestFactory.setHttpClient(httpClient);
        return new RestTemplateBuilder()
                .requestFactory(() -> httpRequestFactory)
                .build();
    }

    @Bean
    public RestTemplate wideshotRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestWithBodyFactory();
        httpRequestFactory.setConnectTimeout(properties.getRestTemplateConnectionTimeout());
        httpRequestFactory.setReadTimeout(properties.getWideshotReadTimeout()); // 와이드샷 timeout 설정
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(properties.getRestTemplateMaxConnectionCount())
                .setMaxConnPerRoute(properties.getRestTemplateMaxConnectionPerRoute()) // connection pool 적용
                .build();
        httpRequestFactory.setHttpClient(httpClient);
        return new RestTemplateBuilder()
                .requestFactory(() -> httpRequestFactory)
                .build();
    }
}
