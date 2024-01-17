package com.platfos.pongift.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WebController {
    @RequestMapping(path = "/", method = RequestMethod.GET, produces= MediaType.TEXT_HTML_VALUE)
    public String index(){
        return "";
    }

    @RequestMapping(path = "/favicon.ico", method = RequestMethod.GET, produces= MediaType.TEXT_HTML_VALUE)
    public String favicon(HttpServletRequest request) {
        return "";
    }
}
