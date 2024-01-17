package com.platfos.pongift.validate.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamRequireDependence {
    private String field;
    private String value;
}
