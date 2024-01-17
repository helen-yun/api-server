package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PK 정보
 */
@Getter
@AllArgsConstructor
public enum SIMIndex {
    AIM_MENU_INFO("MN","aim_menu_info_tb"),
    CIM_LEDGER("GL", "cim_ledger_list_tb"),
    CIM_LEDGER_HIS("LH", "cim_ledger_his_tb"),
    GIM_CHANNEL_INFO("HF", "gim_channel_info_tb"),
    GIM_CONTROL_HIS("CH", "gim_control_his_tb"),
    GIM_GOODS_CONTROL("GC", "gim_goods_control_tb"),
    GIM_GOODS("MC", "gim_goods_list_tb"),
    GIM_GOODS_SUB("CP", "gim_goods_sub_tb"),
    MIM_MERCHANT("MM", "mim_merchant_list_tb"),
    MIM_SERVICE("SM", "mim_service_list_tb"),
    MSM_OPERATE_INFO("ON","msm_operate_info_tb"),
    MSM_STORE("SA", "msm_store_list_tb"),
    POM_CUSTOMER_INFO("CN", "pom_customer_info_tb"),
    POM_DISPATCH_INFO("SN", "pom_dispatch_info_tb"),
    POM_TRADE_GIFT("TG", "pom_trade_gift_tb"),
    POM_TRADE("TN", "pom_trade_list_tb"),
    PUM_USER("UM", "pum_user_list_tb"),
    SIM_ATTACH_FILE("AF", "sim_attach_file_tb"),
    SIM_TEMPLATE_INFO("TF", "sim_template_info_tb"),
    SIM_TRANSMIT_INFO("RF","sim_transmit_info_tb"),
    SSM_GOODS_INFO("SG", "ssm_goods_info_tb"),
    SSM_GIFTCARD_SEND_LIST("SI", "ssm_send_list_tb"),
    SSM_GIFTCARD_SEND_HIS("SH", "ssm_send_his_tb"),
    SSM_GIFTCARD_HIS("GH", "ssm_giftcard_his_tb")
    ;

    private String code;
    private String tbl;

    public static SIMIndex findByCode(String code){
        if(code == null) return null;

        for(SIMIndex simIndex : values()){
            if(code.equals(simIndex.getCode())){
                return simIndex;
            }
        }
        return null;
    }

}
