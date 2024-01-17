package com.platfos.pongift.security;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

public class SHA256Util {
    public static String encode(String msg)  throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());

        return byteToHexString(md.digest());


        //return new String(Base64.encodeBase64(md.digest()));
    }
    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for(byte b : data) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }
}
