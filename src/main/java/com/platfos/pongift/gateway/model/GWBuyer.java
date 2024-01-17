package com.platfos.pongift.gateway.model;

import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.PhoneNumber;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimCode;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWBuyer {
    @Description("구매자 ID")
    private String buyerId;

    @Require
    @Length(max = 100)
    @Description("구매자 이름")
    private String name;

    @Require
    @Length(max = 12)
    @PhoneNumber
    @Description("구매자 휴대폰번호")
    private String phoneNo;

    @Length(max = 200)
    @Email
    @Description("구매자 이메일 주소")
    private String emailAddress;

    @SimCode(groupCode = { SIMCodeGroup.paymentGb })
    @Description("구매자 결제 수단 구분")
    private String paymentGb;

}
