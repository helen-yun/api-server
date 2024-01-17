package com.platfos.pongift.gateway.model;

import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.constraints.PhoneNumber;
import com.platfos.pongift.validate.constraints.Require;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GWReceiver {
    @Require
    @Length(max = 100)
    @Description("수신자 이름")
    private String name;

    @Require
    @Length(max = 12)
    @PhoneNumber
    @Description("수신자 휴대폰번호")
    private String phoneNo;

    @Length(max = 200)
    @Email
    @Description("수신자 이메일 주소")
    private String emailAddress;
}
