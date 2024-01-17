package com.platfos.pongift.exception.controller;

import com.platfos.pongift.definition.AttributeKey;
import com.platfos.pongift.definition.AuthorizationHeaderKey;
import com.platfos.pongift.exception.WebException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** WebController Error Custom **/
@Controller
@RequestMapping()
public class WebErrorController extends BasicErrorController {
    private static final Logger logger = LoggerFactory.getLogger(WebErrorController.class);

    public WebErrorController(ErrorAttributes errorAttributes,
                                 ServerProperties serverProperties,
                                 List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
    }

    @RequestMapping(path = "error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response) throws WebException {
        //에러 메시지 바디 생성
        Map<String, Object> body = this.getErrorAttributes(request, this.getTraceParameter(request));

        if (request.getAttribute(AttributeKey.DATE_TIME_KEY.getKey()) != null) {
            try {
                body.put(AttributeKey.DATE_TIME_KEY.getKey(), request.getAttribute(AttributeKey.DATE_TIME_KEY.getKey()));
            } catch (Exception e) {e.printStackTrace();}
        }
        if (request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey()) != null) {
            body.put(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey(), request.getHeader(AuthorizationHeaderKey.SERVICE_ID_KEY.getKey()));
        }
        if (request.getHeader(AuthorizationHeaderKey.STORE_ID_KEY.getKey()) != null) {
            body.put(AuthorizationHeaderKey.STORE_ID_KEY.getKey(), request.getHeader(AuthorizationHeaderKey.STORE_ID_KEY.getKey()));
        }
        if (request.getAttribute(AttributeKey.IP_ADDRESS_KEY.getKey()) != null) {
            body.put(AttributeKey.IP_ADDRESS_KEY.getKey(), request.getAttribute(AttributeKey.IP_ADDRESS_KEY.getKey()));
        }
        if (request.getAttribute("javax.servlet.error.exception") != null) {
            Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
            body.put("trace", ExceptionUtils.getStackTrace(t));
        }

        throw new WebException(body);
    }
}
