package com.platfos.pongift.definition;

import lombok.Getter;

/**
 * API 공통 응답 코드
 */
@Getter
public enum ResponseCode {
    SUCCESS("api.response.success"),
    FAIL("api.response.fail"),
    FAIL_NO_AUTH("api.response.fail_no_auth"),
    FAIL_METHOD_NOT_ALLOWED("api.response.fail_method_not_allowed"),
    FAIL_UNSUPPORTED_MEDIA_TYPE("api.response.fail_unsupported_media_type"),
    FAIL_MISSING_REQUEST_PARAMETER("api.response.fail_missing_request_parameter"),
    FAIL_ILLEGAL_REQUEST_PARAMETER("api.response.fail_illegal_request_parameter"),
    FAIL_GOODS_CHANNEL_WORKING("api.response.fail_goods_channel_working"),
    FAIL_GOODS_CHANNEL_ERROR("api.response.fail_goods_channel_error"),
    FAIL_PRODUCTS_ON_DISPLAY("api.response.fail_products_on_display"),
    FAIL_GOODS_EXHIBIT_ST_WAITING("api.response.fail_goods_exhibit_st_waiting"),
    FAIL_STORE_SERVICE_ST("api.response.fail_store_service_st"),
    FAIL_GOODS_EXHIBIT_NO_APPROVAL_ST("api.response.fail_goods_exhibit_no_approval_st"),
    FAIL_GIFT_CANNOT_BE_UPDATE_ST("api.response.fail_gift_cannot_be_update_st"),
    FAIL_GIFT_CANNOT_BE_USED_STORE("api.response.fail_gift_cannot_be_used_store"),
    FAIL_GIFT_CANNOT_BE_CANCELED_USE("api.response.fail_gift_cannot_be_canceled_use"),
    FAIL_GIFT_EXPIRATION("api.response.fail_gift_expiration"),
    FAIL_GIFT_BARCODE_NOT_EXIST("api.response.fail_gift_barcode_not_exist"),
    FAIL_GIFT_CANNOT_BE_CANCELED_USE_EXPIRATION("api.response.fail_gift_cannot_be_canceled_use_expiration"),
    FAIL_GIFT_RESEND_COUNT_EXCEEDED("api.response.fail_gift_resend_count_exceeded"),
    FAIL_GIFT_SERVICE_ST_NOT_USED("api.response.fail_gift_service_st_not_used"),
    FAIL_GIFT_NULL_POINTER_EXCEPTION("api.response.fail_gift_null_pointer_exception"),
    FAIL_TRADE_REQUEST_DUPLICATE("api.response.fail_trade_request_duplicate"),
    FAIL_OUT_OF_STOCK("api.response.fail_out_of_stock"),
    ;
    private final String messageKey;
    ResponseCode(String messageKey){
        this.messageKey = messageKey;
    }

}
