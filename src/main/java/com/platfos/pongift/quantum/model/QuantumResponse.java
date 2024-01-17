package com.platfos.pongift.quantum.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuantumResponse {
    /** 성공 여부 **/
    private boolean success;
    /** 응답 메시지 **/
    private String message;
    /** quantum data 사이즈 **/
    private int dataSize;
    /** quantum data **/
    private String data;
}
