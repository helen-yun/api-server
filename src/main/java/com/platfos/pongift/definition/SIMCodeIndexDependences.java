package com.platfos.pongift.definition;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SIMCodeIndexDependences {
    public static final Map<SIMCodeGroup, Map<String, SIMIndex>> dependences =
            ImmutableMap.of(
                /** 서비스 대행사 유무 - 대행사 코드(서비스 아이디) **/
                SIMCodeGroup.agencyFl, ImmutableMap.of(
                        "Y", SIMIndex.MIM_SERVICE,
                        "N", null)
    );
}
