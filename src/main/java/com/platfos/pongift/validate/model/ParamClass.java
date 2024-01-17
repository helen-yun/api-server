package com.platfos.pongift.validate.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamClass {
    private Class<?> clzz;
    private String name;
    private List<ParamMethod> methods;
}
