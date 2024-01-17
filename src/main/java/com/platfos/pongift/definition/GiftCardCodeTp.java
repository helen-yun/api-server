package com.platfos.pongift.definition;

import com.google.zxing.BarcodeFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품권 코드 타입
 */
@Getter
@AllArgsConstructor
public enum GiftCardCodeTp {
    CODE_39("01", BarcodeFormat.CODE_39),
    CODE_128("02", BarcodeFormat.CODE_128),
    AZTEC("03", BarcodeFormat.AZTEC),
    CODABAR("04", BarcodeFormat.CODABAR),
    CODE_93("05", BarcodeFormat.CODE_93),
    DATA_MATRIX("06", BarcodeFormat.DATA_MATRIX),
    EAN_8("07", BarcodeFormat.EAN_8),
    EAN_13("08", BarcodeFormat.EAN_13),
    ITF("09", BarcodeFormat.ITF),
    MAXICODE("10", BarcodeFormat.MAXICODE),
    PDF_417("11", BarcodeFormat.PDF_417),
    QR_CODE("12", BarcodeFormat.QR_CODE),
    RSS_14("13", BarcodeFormat.RSS_14),
    RSS_EXPANDED("14", BarcodeFormat.RSS_EXPANDED),
    UPC_A("15", BarcodeFormat.UPC_A),
    UPC_E("16", BarcodeFormat.UPC_E),
    UPC_EAN_EXTENSION("17", BarcodeFormat.UPC_EAN_EXTENSION);

    private String code;
    private BarcodeFormat format;

    public static GiftCardCodeTp findByCode(String code){
        if(code == null) return null;

        for(GiftCardCodeTp barcodeTp : values()){
            if(code.equals(barcodeTp.getCode())){
                return barcodeTp;
            }
        }
        return null;
    }
}
