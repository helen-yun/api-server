package com.platfos.pongift.store.model;

import com.platfos.pongift.definition.SIMIndex;
import com.platfos.pongift.validate.annotation.Description;
import com.platfos.pongift.validate.annotation.ParamGroup;
import com.platfos.pongift.validate.constraints.Require;
import com.platfos.pongift.validate.constraints.SimIndex;
import com.platfos.pongift.validate.group.GroupB;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreId {
    @ParamGroup(groups = { GroupB.class})
    @Require(groups = { GroupB.class})
    @SimIndex(simIndex = SIMIndex.MSM_STORE)
    @Description("매장ID")
    private String storeId;
}
