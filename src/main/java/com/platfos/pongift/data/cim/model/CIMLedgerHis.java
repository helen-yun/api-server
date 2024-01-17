package com.platfos.pongift.data.cim.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CIMLedgerHis {
    private String modifyId;
    private String ledgerId;
    private String modifyTp;
    private String expiryDt;
    private String giftSt;
    private int modifyCnt;
    private Date regDate;
}
