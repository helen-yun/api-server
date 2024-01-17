package com.platfos.pongift.config;

import com.platfos.pongift.authorization.filter.APIPermissionAuthFilter;
import com.platfos.pongift.authorization.interceptor.APIPermissionAuthInterceptor;
import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.config.properties.Cors;
import com.platfos.pongift.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    ApplicationProperties properties;

    @Autowired
    APIPermissionAuthInterceptor permissionAuthInterceptor;

    @Autowired
    GoodsService goodsService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if(properties != null && properties.getCors() != null){
            for(Cors item : properties.getCors()){
                registry.addMapping(item.getPath()).allowedOrigins(item.getOrigins().toArray(new String[0])).allowCredentials(true);
            }
        }
    }

    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadImagePath = properties.getAttachUploadPath();
        if(!uploadImagePath.endsWith("/")) uploadImagePath = uploadImagePath + "/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///"+uploadImagePath+"/");
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionAuthInterceptor);
                /*.addPathPatterns("/api/**")
                .addPathPatterns("/upload/**")
                .excludePathPatterns("/api/monitors");*/
    }

    @Bean
    public FilterRegistrationBean getFilterRegistrationBean()
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new APIPermissionAuthFilter());
        return registrationBean;
    }
}
