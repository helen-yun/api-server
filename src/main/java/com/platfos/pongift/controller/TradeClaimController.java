package com.platfos.pongift.controller;

import com.platfos.pongift.authorization.annotation.APIPermission;
import com.platfos.pongift.definition.APIRole;
import com.platfos.pongift.response.model.BaseResponse;
import com.platfos.pongift.trade.service.TradeService;
import com.platfos.pongift.validate.annotation.ParamClzz;
import com.platfos.pongift.validate.annotation.ParamMethodValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@ParamClzz(name = "")
public class TradeClaimController {
    private final TradeService tradeService;
  // TODO:  

}
