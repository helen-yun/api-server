package com.platfos.pongift.document.model;

import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.definition.SIMCodeGroup;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocCode {
    /** 코드명 **/
    private String name;
    /** 부모 그룹 코드(의존 관계) **/
    private SIMCodeGroup parentCodeGroup;
    /** 부모 코드 값(의존 관계) **/
    private String parentValue;
    /** 부모 코드 (의존 관계) 여부 **/
    private boolean isNotUseParentURL;
    /** 접근 가능 권한 리스트 **/
    private List<String> roles;
    /** 코드 리스트 **/
    private List<SIMCode> docCodes;
}
