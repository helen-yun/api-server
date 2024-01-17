package com.platfos.pongift.data.cim.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CIMCancelableUseLedger {
    public String period;
    public boolean cancelableUse;
}
