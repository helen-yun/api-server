package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 첨부 파일 관리/파일 구분
 */
@Getter
@AllArgsConstructor
public enum SIMAttach {
    GOODS("01", "01", "goodsImage"),
    GOODS_DETAIL("01", "02", "goodsDetailImage"),
    STORE_01("02", "01", "storeImage1"),
    STORE_02("02", "02", "storeImage2"),
    STORE_03("02", "03", "storeImage3"),
    BOARD_PARTNER_GUIDE("03", "01", "partnerGuideImage"),
    BOARD_NOTICE("03", "02", "noticeImage"),
    BOARD_ERROR("03", "03", "errorImage");

    private String maintainGb;
    private String fileGb;
    private String filedNm;
}
