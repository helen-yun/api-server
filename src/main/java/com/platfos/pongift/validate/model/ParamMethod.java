package com.platfos.pongift.validate.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamMethod {
    private String name;
    private String path;
    private String method;
    private String methodName;
    private String contentType;
    private ParamField response;
    private List<ParamField> request;
}
