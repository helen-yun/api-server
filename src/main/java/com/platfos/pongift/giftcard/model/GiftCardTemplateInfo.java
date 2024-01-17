package com.platfos.pongift.giftcard.model;

import com.platfos.pongift.data.sim.model.SIMTemplateInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCardTemplateInfo {
    private SIMTemplateInfo template;
    private byte[] bytes;
}
