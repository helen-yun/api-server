package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 승인 상태
 *  (01:미승인, 02:승인, 03:반려)
 */
@Getter
@AllArgsConstructor
public enum GoodsApprovalSt {
    DISAPPROVAL("01"),
    APPROVAL("02"),
    COMPANION("03");

    private String code;

    public static GoodsApprovalSt findByCode(String code){
        if(code == null) return null;

        for(GoodsApprovalSt approvalSt : values()){
            if(code.equals(approvalSt.getCode())){
                return approvalSt;
            }
        }
        return null;
    }
}
