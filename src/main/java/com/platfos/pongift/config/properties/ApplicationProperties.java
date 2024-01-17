package com.platfos.pongift.config.properties;

import com.platfos.pongift.util.Util;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Application Properties from application.properties
 * 시스템 환경 변수
 */
@Component
@ConfigurationProperties(prefix = "platfos", ignoreInvalidFields = true)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApplicationProperties {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);
    private static final String DEFAULT_MAX_SIZE = "5MB";

    /**
     * 시스템 모드(01:운영, 02:개발)
     **/
    private String systemMode;
    /**
     * WideShot 시스템 모드(01:운영, 02:개발)
     **/
    private String wideshotMode;
    /**
     * 최대 응답 대기 시간(문자 전송 등록 일시 기준, 단위:초)
     **/
    private Integer wideshotResultTimeOver;
    /**
     * 와이드샷 TimeOut 기본값 (단위:ms)
     **/
    private int wideshotReadTimeout;

    /**
     * 첨부파일 지원 가능 포멧 맵(내부 시스템용)
     **/
    private Map<String, String> mimeTypeMap;
    /**
     * 첨부파일 지원 가능 포멧 맵(properties 설정용)
     **/
    private Map<String, String> mimetype;

    /**
     * UploadMaxSize
     **/
    private String attachUploadMaxSize;

    /**
     * CORS 설정
     **/
    private List<Cors> cors;

    /**
     * 내부 연동 IP 리스트
     **/
    private List<String> accessible;

    /**
     * 유통 채널 IP 리스트
     **/
    private List<String> channels;

    /**
     * API 서버 IP 리스트(L4)
     **/
    private List<String> ips;
    private String serverName;
    private String serverProtocol;
    private String serverIp;
    @Value("${server.port:80}")
    private String serverPort;
    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    /**** RestTemplate 설정 ****/
    /**
     * Connection Timeout 기본값, 단위는 ms
     **/
    private static final int DEFAULT_HTTP_CLIENT_CONN_TIMEOUT = 1000; //테스트해보니 설정값 2배가 적용됨. (Naver 선물하기 스케쥴이 2초라 1000으로 설정함.)
    private Integer restTemplateConnectionTimeout; //Connection Timeout

    /**
     * Read Timeout 기본값, 단위는 ms
     **/
    private static final int DEFAULT_HTTP_CLIENT_READ_TIMEOUT = 15000; //html convert to image 작업 시 8초 정도 소요
    private Integer restTemplateReadTimeout;  //Read Timeout

    /**
     * Max Connection Count 기본값
     **/
    private static final int DEFAULT_HTTP_CLIENT_MAX_CONN = 200;
    private Integer restTemplateMaxConnectionCount; //Max Connection Count

    /**
     * Route 당 Max Connection Count 기본값
     **/
    private static final int DEFAULT_HTTP_CLIENT_MAX_CONN_PER_ROUTE = 20;
    private Integer restTemplateMaxConnectionPerRoute; //Route 당 Max Connection Count

    /**
     * 기본 언어
     **/
    private String defaultLanguage;

    @Value("${spring.servlet.multipart.max-file-size:#{null}}")
    private String multipartMaxFileSize;

    /**
     * API Document HTML 경로
     **/
    private String apiDoc;

    private String phantomjsPath;

    public ApplicationProperties() {
        mimeTypeMap = new HashMap<>();
        mimeTypeMap.put("image/bmp", "bmp");
        mimeTypeMap.put("image/jpeg", "jpg");
        mimeTypeMap.put("image/png", "png");
        mimeTypeMap.put("image/gif", "gif");
        mimeTypeMap.put("image/tiff", "tiff");
        mimeTypeMap.put("image/vnd.adobe.photoshop", "psd");
        mimeTypeMap.put("text/plain", "txt");
        mimeTypeMap.put("application/java-archive", "jar");
        mimeTypeMap.put("application/zip", "zip");
        mimeTypeMap.put("application/x-msdos-program", "exe");
        mimeTypeMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        mimeTypeMap.put("application/vnd.ms-excel", "xls");
        mimeTypeMap.put("application/msword", "doc");
        mimeTypeMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        mimeTypeMap.put("application/vnd.ms-powerpoint", "ppt");
        mimeTypeMap.put("application/vnd.openxmlformats-officedocument.presentationml.slideshow", "ppsx");
        mimeTypeMap.put("application/pdf", "pdf");

        mimeTypeMap.put("application/x-hwp", "hwp");
        mimeTypeMap.put("application/haansofthwp", "hwp");
        mimeTypeMap.put("application/vnd.hancom.hwp", "hwp");

        //if(goodsImageUpdate == null) goodsImageUpdate = false;
    }

    @PostConstruct
    public void postConstruct() {
        if (mimetype != null && mimetype.size() > 0) {
            mimeTypeMap.putAll(mimetype);
        }

        if (!StringUtils.isEmpty(multipartMaxFileSize)) {
            attachUploadMaxSize = multipartMaxFileSize;
        }
        if (StringUtils.isEmpty(attachUploadMaxSize)) {
            attachUploadMaxSize = DEFAULT_MAX_SIZE;
        }

        if (restTemplateConnectionTimeout == null) restTemplateConnectionTimeout = DEFAULT_HTTP_CLIENT_CONN_TIMEOUT;
        if (restTemplateReadTimeout == null) restTemplateReadTimeout = DEFAULT_HTTP_CLIENT_READ_TIMEOUT;
        if (restTemplateMaxConnectionCount == null) restTemplateMaxConnectionCount = DEFAULT_HTTP_CLIENT_MAX_CONN;
        if (restTemplateMaxConnectionPerRoute == null)
            restTemplateMaxConnectionPerRoute = DEFAULT_HTTP_CLIENT_MAX_CONN_PER_ROUTE;

        InetAddress local;
        try {
            local = InetAddress.getLocalHost();
            serverIp = local.getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        if (!sslEnabled) serverProtocol = "http";
        else serverProtocol = "https";

        if (StringUtils.isEmpty(serverName)) {
            serverName = "API-SERVER";
            if (!ObjectUtils.isEmpty(ips)) {
                for (int i = 0; i < ips.size(); i++) {
                    String ip = ips.get(i);
                    if (ip != null && ip.equals(serverIp)) {
                        serverName = serverName + "-" + (i + 1);
                        break;
                    }
                }
            }
        }
        print();
    }

    public void print() {
        int chappterIdx = 1;
        int idx = 1;

        logger.info("Server Protocol => " + serverProtocol);
        logger.info("Server IP => " + serverIp);
        logger.info("Server Name => " + serverName);

        if (ips != null && ips.size() > 0) {
            logger.info(chappterIdx + ". API Server IP List :::::::::::::::::::::");
            logger.info("API Servers => " + StringUtils.join(ips, ','));
            chappterIdx++;
        }

        if (cors != null && cors.size() > 0) {
            logger.info(chappterIdx + ". CORS List ::::::::::::::::::::::::::::::::::::::::::::::::");
            for (Cors item : cors) {
                logger.info(Util.lpad(String.valueOf(idx), 2, "0") + ") " + item.getPath() + " => " + StringUtils.join(item.getOrigins(), ','));
                idx++;
            }
            chappterIdx++;
        }

        if (accessible != null && accessible.size() > 0) {
            logger.info(chappterIdx + ". Authorization Externally Accessibles :::::::::::::::::::::");
            logger.info("accessibles => " + StringUtils.join(accessible, ','));
            chappterIdx++;
        }

        if (channels != null && channels.size() > 0) {
            logger.info(chappterIdx + ". Distribution Channels :::::::::::::::::::::");
            logger.info("channels => " + StringUtils.join(channels, ','));
            chappterIdx++;
        }

        logger.info(chappterIdx + ". RestTemplate Config :::::::::::::::::::::");
        logger.info("Connection Timeout => " + restTemplateConnectionTimeout + "ms");
        logger.info("Read Timeout => " + restTemplateReadTimeout + "ms");
        logger.info("WideShot Read Timeout => " + wideshotReadTimeout + "ms");
        logger.info("Max Connection Count => " + restTemplateMaxConnectionCount);
        logger.info("Max Connection Per Route => " + restTemplateMaxConnectionPerRoute);
        chappterIdx++;

        logger.info(chappterIdx + ". Attach Upload Properties :::::::::::::::::::::::::::::::::");
        //if(!StringUtils.isEmpty(attachUploadPath)) logger.info("upload path => " + attachUploadPath);
        //if(!StringUtils.isEmpty(attachUploadHost)) logger.info("upload host => " + attachUploadHost);
        logger.info("upload maxSize => " + attachUploadMaxSize);
        chappterIdx++;

        Set<String> set = getMimeTypeMap().keySet();
        Iterator<String> iterator = set.iterator();
        logger.info(chappterIdx + ". Attach MimeType Map ::::::::::::::::::::::::::::::::::::::");
        idx = 1;
        while (iterator.hasNext()) {
            String key = iterator.next();
            logger.info(Util.lpad(String.valueOf(idx), 2, "0") + ") " + key + " => " + mimeTypeMap.get(key));
            idx++;
        }
        chappterIdx++;

        idx = 1;
        logger.info(chappterIdx + ". ETC ::::::::::::::::::::::::::::::::::::::");
        logger.info(Util.lpad(String.valueOf(idx), 2, "0") + ") " + "Default Language => " + defaultLanguage);
        idx++;
    }

    public TreeMap<String, String> getMimeTypeMap() {
        return new TreeMap<String, String>(mimeTypeMap);
    }

    public String getExtension(String mimeType) {
        if (StringUtils.isEmpty(mimeType)) return null;
        if (!mimeTypeMap.containsKey(mimeType)) return null;
        return mimeTypeMap.get(mimeType);
    }

    public String getMimeType(String fileName) {
        if (StringUtils.isEmpty(fileName)) return null;

        int dotLastIndex = fileName.lastIndexOf(".");
        if (dotLastIndex < 0) return null;

        String fileExtension = fileName.substring(dotLastIndex + 1).toLowerCase();
        if (StringUtils.isEmpty(fileExtension)) return null;

        return getMimeTypeByExtension(fileExtension);
    }

    public String getMimeTypeByExtension(String extension) {
        Set<String> set = mimeTypeMap.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String mimeType = it.next();
            if (extension.equalsIgnoreCase(mimeTypeMap.get(mimeType))) return mimeType;
        }
        return null;
    }

    public boolean isImageFile(String extension) {
        String mimeType = getMimeTypeByExtension(extension);

        if (mimeType != null) {
            return mimeType.startsWith("image");
        }
        return false;
    }

    public double uploadMaxSize() {
        return Util.sizeString2Double(attachUploadMaxSize);
    }

    public String strUploadMaxSize() {
        if (Util.isNumeric(attachUploadMaxSize)) {
            return attachUploadMaxSize + " bytes";
        } else {
            int pow = getPow();
            if (pow > 0) return attachUploadMaxSize;
            else return attachUploadMaxSize.replace("B", " bytes");
        }
    }

    private int getPow() {
        int pow = -1;

        if (attachUploadMaxSize.toUpperCase().endsWith("KB")) pow = 1;
        else if (attachUploadMaxSize.toUpperCase().endsWith("MB")) pow = 2;
        else if (attachUploadMaxSize.toUpperCase().endsWith("GB")) pow = 3;
        else if (attachUploadMaxSize.toUpperCase().endsWith("TB")) pow = 4;
        else if (attachUploadMaxSize.toUpperCase().endsWith("PB")) pow = 5;
        else if (attachUploadMaxSize.toUpperCase().endsWith("EB")) pow = 6;
        else if (attachUploadMaxSize.toUpperCase().endsWith("B")) pow = 0;

        return pow;
    }
}
