package com.platfos.pongift.controller;

import com.platfos.pongift.data.aim.service.AIMMessageInfoService;
import com.platfos.pongift.response.model.DataResponse;
import com.platfos.pongift.response.service.APIResponseService;
import com.platfos.pongift.validate.annotation.ParamClzz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@ParamClzz(name = "테스트")
public class TestController {
    @Autowired
    APIResponseService responseService;

    @Autowired
    AIMMessageInfoService messageInfoService;

    @RequestMapping(path="test/messages", method= RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<HashMap<String, String>> channels(HttpServletRequest request) {
        return responseService.data(request, messageInfoService.getMessageResources());
    }
}
