package com.platfos.pongift.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AES256Util {
    private final static String SECRET_KEY = "PongiftPhoneDegepVariety";

    private static Cipher cipher(int mode, String encoding) throws Exception {
        byte[] keyBytes = new byte[16];
        byte[] b = SECRET_KEY.getBytes(encoding);
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        Key key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, key, new IvParameterSpec(SECRET_KEY.substring(0, 16).getBytes(encoding)));
        return cipher;
    }

    public static String encode(String str) throws Exception {
        return encode(str, "UTF-8");
    }

    public static String encode(String str, String encoding) throws Exception {
        if (str == null) {
            return "";
        }
        Cipher cipher = cipher(Cipher.ENCRYPT_MODE, encoding);

        byte[] encrypted = cipher.doFinal(str.getBytes(encoding));
        String enStr = new String(Base64.encodeBase64(encrypted));

        return enStr;
    }

    public static String decode(String str) throws Exception {
        return decode(str, "UTF-8");
    }

    public static String decode(String str, String encoding) throws Exception {
        Cipher cipher = cipher(Cipher.DECRYPT_MODE, encoding);

        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        return new String(cipher.doFinal(byteStr), encoding);
    }

    /**
     * AES256 암호화 후 hex code로 encoding
     **/
    public static String encodeHex(String str) throws Exception {
        return encodeHex(str, "UTF-8");
    }

    /**
     * AES256 암호화 후 hex code로 encoding
     **/
    public static String encodeHex(String str, String encoding) throws Exception {
        Cipher cipher = cipher(Cipher.ENCRYPT_MODE, encoding);

        byte[] encrypted = cipher.doFinal(str.getBytes(encoding));
        String enStr = Hex.encodeHexString(encrypted);

        return enStr;
    }

    /**
     * hex code decoding 후 AES256으로 복호화
     **/
    public static String decodeHex(String str) throws Exception {
        return decodeHex(str, "UTF-8");
    }

    /**
     * hex code decoding 후 AES256으로 복호화
     **/
    public static String decodeHex(String str, String encoding) throws Exception {
        Cipher cipher = cipher(Cipher.DECRYPT_MODE, encoding);

        byte[] byteStr = Hex.decodeHex(str);
        return new String(cipher.doFinal(byteStr), encoding);
    }

    /**
     * 암호화, 이전 값 검증
     *
     * @param encryptedStr
     * @param previousStr
     * @return
     * @throws Exception
     */
    public static boolean verifyDecryptedValue(String encryptedStr, String previousStr) throws Exception {
        String decryptedStr = decode(encryptedStr);
        return decryptedStr.equals(previousStr);
    }

    /**
     * 암호화, 이전 값 검증 (반복)
     * @param encryptedStr
     * @param previousStr
     * @return
     * @throws Exception
     */
    public static boolean verifyMultipleAttempts(String encryptedStr, String previousStr) throws Exception {
        int maxAttempts = 5;
        for (int i = 0; i < maxAttempts; i++) {
            boolean isValid = verifyDecryptedValue(encryptedStr, previousStr);
            if (isValid) {
                return true; // 검증 성공 시 true 반환
            }
        }
        return false;
    }

}

