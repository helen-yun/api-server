package com.platfos.pongift.util;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final int FILE_SIZE_UNIT = 1024;
    public static final int RND_MODE_NUMBER = 0;
    public static final int RND_MODE_LOWER = 1;
    public static final int RND_MODE_UPPER = 2;
    public static final int RND_MODE_NUMBER_LOWER = 3;
    public static final int RND_MODE_NUMBER_UPPER = 4;
    public static final int RND_MODE_NUMBER_LOWER_UPPER = 5;
    public static final int RND_MODE_LOWER_UPPER = 6;

    public static String strToPhoneNumber(String src) {
        if (src == null) {
            return "";
        }

        src = src.replaceAll(" ", "");

        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }

    public static String logJsonSecretFilter(String log, String attrNm, String replace){
        if(StringUtils.isEmpty(log)) return log;
        String[] patterns = {"\""+attrNm+"\" : \"(.+?)\"", "\""+attrNm+"\":\"(.+?)\""};
        for(String pattern : patterns){
            Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);

            Matcher matcher = regex.matcher(log);
            while (matcher.find()) {
                String val = matcher.group(1);
                log = log.replace(pattern.replace("(.+?)", val), pattern.replace("\"(.+?)\"", replace));
            }
        }
        return log;
    }

    public static boolean isNumeric(String str){
        boolean numeric = true;
        try {
            Double num = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        return numeric;
    }

    public static int powSizeStr(String str){
        int pow = -1;

        if(str.toUpperCase().endsWith("KB")) pow = 1;
        else if(str.toUpperCase().endsWith("MB")) pow = 2;
        else if(str.toUpperCase().endsWith("GB")) pow = 3;
        else if(str.toUpperCase().endsWith("TB")) pow = 4;
        else if(str.toUpperCase().endsWith("PB")) pow = 5;
        else if(str.toUpperCase().endsWith("EB")) pow = 6;
        else if(str.toUpperCase().endsWith("B")) pow = 0;

        return pow;
    }
    public static String doubleSize2String(double size){
        if(size<FILE_SIZE_UNIT) return String.format("%,.3f",size) + " bytes";
        for(int i=1;i<7;i++){
            int pow = i+1;
            double tmp = Math.pow(FILE_SIZE_UNIT, pow);
            if(size<tmp){
                String unit = "";
                switch (i){
                    case 1 : unit = "KB"; break;
                    case 2 : unit = "MB"; break;
                    case 3 : unit = "GB"; break;
                    case 4 : unit = "TB"; break;
                    case 5 : unit = "PB"; break;
                    case 6 : unit = "EB"; break;
                }
                return String.format("%,.3f", size/Math.pow(FILE_SIZE_UNIT, i))+unit;
            }
        }
        return String.valueOf(size);
    }
    public static double sizeString2Double(String str){
        double rs = 0;

        if(isNumeric(str)){
            try{ rs = Double.parseDouble(str); }catch (Exception e){e.printStackTrace();}
        }else{
            int pow = powSizeStr(str);
            double size = 0;
            if(pow > -1){
                try { size = ((pow>0)? Double.parseDouble(str.substring(0, str.length() - 2)) : Double.parseDouble(str.substring(0, str.length() - 1))); } catch (Exception e){e.printStackTrace();}
                rs = (size*((pow>0)?Math.pow(FILE_SIZE_UNIT, pow):1));
            }
        }
        return rs;
    }

    public static double numberstr2long(String numberstr) throws ParseException {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.parse(numberstr).longValue();
    }
    public static int bytes2filesize(byte[] bytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(bytes);

        byte[] imageInBytes = bos.toByteArray();
        int length = imageInBytes.length;

        return length;
    }
    public static String timestamp2strdate(long timestamp, String format) throws Exception{
        DateFormat sdf = new SimpleDateFormat(format);
        Date date = (new Date(timestamp));
        return sdf.format(date);
    }
    public static long strdate2timestamp(String strdate, String format) throws Exception{
        //yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(strdate).getTime();
    }
    public static String lpad(String str, int len, String chr){
        StringBuilder buff = new StringBuilder();
        for(int i = str.length(); i < len; i++ ) buff.append(chr);
        return (buff + str);
    }
    public static String utf8Cut(String s, int n) {
        byte[] utf8 = s.getBytes();
        if (utf8.length < n) n = utf8.length;
        int n16 = 0;
        int advance = 1;
        int i = 0;
        while (i < n) {
            advance = 1;
            if ((utf8[i] & 0x80) == 0) i += 1;
            else if ((utf8[i] & 0xE0) == 0xC0) i += 2;
            else if ((utf8[i] & 0xF0) == 0xE0) i += 3;
            else { i += 4; advance = 2; }
            if (i <= n) n16 += advance;
        }
        return s.substring(0,n16);
    }

    public static String random(int mode, int length) {
        if (mode < 0 | mode > 6) return null;
        if (length < 1) return null;

        String dataLower = "abcdefghijklmnopqrstuvwxyz";
        String dataUpper = dataLower.toUpperCase();
        String dataNumber = "0123456789";

        SecureRandom random = new SecureRandom();
        String data = null;

        switch (mode){
            case RND_MODE_NUMBER : data = dataNumber; break;
            case RND_MODE_LOWER : data = dataLower; break;
            case RND_MODE_UPPER : data = dataUpper; break;
            case RND_MODE_NUMBER_LOWER : data = dataNumber+dataLower; break;
            case RND_MODE_NUMBER_UPPER : data = dataNumber+dataUpper; break;
            case RND_MODE_NUMBER_LOWER_UPPER : data = dataNumber+dataLower+dataUpper; break;
            case RND_MODE_LOWER_UPPER : data = dataLower+dataUpper; break;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) sb.append(data.charAt(random.nextInt(data.length())));
        return sb.toString();
    }
}
