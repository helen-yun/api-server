package com.platfos.pongift.validate.validator;

import com.platfos.pongift.config.properties.ApplicationProperties;
import com.platfos.pongift.data.sim.model.SIMCode;
import com.platfos.pongift.data.sim.service.SIMCodeService;
import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.definition.SIMCodeGroup;
import com.platfos.pongift.util.Util;
import com.platfos.pongift.validate.constraints.File;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileValidator implements ConstraintValidator<File, Object> {
    @Autowired
    ApplicationProperties properties;
    @Autowired
    SIMCodeService simCodeService;
    @Autowired
    RestTemplate restTemplate;

    String maxFileSize;
    int minImageWidth;
    int minImageHeight;
    int maxImageWidth;
    int maxImageHeight;
    private String[] supportFileFormat;
    private AttachDataType[] supportAttachType;

    @Override
    public void initialize(File annotation) {
        maxFileSize = annotation.maxFileSize();
        minImageWidth = annotation.minImageWidth();
        minImageHeight = annotation.minImageHeight();
        maxImageWidth = annotation.maxImageWidth();
        maxImageHeight = annotation.maxImageHeight();
        supportFileFormat = annotation.supportFileFormat();
        supportAttachType = annotation.supportAttachType();
    }

    @Override
    public boolean isValid(Object val, ConstraintValidatorContext ctx) {
        if(!ObjectUtils.isEmpty(val)){
            if(val instanceof String){
                return isValidData((String) val, ctx);
            }else if(val instanceof MultipartFile){
                if(!isValidSupportAttachType(AttachDataType.BINARY)) return validFail("Unsupported Data Type : supported -> "+StringUtils.join(supportAttachType, ','), ctx);
                return isValidFile((MultipartFile) val, ctx);
            }else if(val instanceof List){
                for(Object elem : (List) val){
                    if(elem instanceof String) {
                        return isValidData((String) elem, ctx);
                    }else if(elem instanceof MultipartFile){
                        if(!isValidSupportAttachType(AttachDataType.BINARY)) return validFail("Unsupported Data Type : supported -> "+StringUtils.join(supportAttachType, ','), ctx);
                        return isValidFile((MultipartFile) elem, ctx);
                    }
                }
            }
        }
        return true;
    }

    private boolean isValidSupportAttachType(AttachDataType attachDataType){
        if(ObjectUtils.isEmpty(supportAttachType)) return true;

        for(AttachDataType type : supportAttachType){
            if(type == attachDataType) return true;
        }
        return false;
    }

    private boolean isValidFile(MultipartFile val, ConstraintValidatorContext ctx){
        byte[] bytes = null;
        try { bytes = val.getBytes(); } catch (IOException e) {e.printStackTrace();}

        String extension = properties.getExtension(val.getContentType().toLowerCase());

        if(ObjectUtils.isEmpty(bytes)){
            return validFail("Not Found File", ctx);
        }

        return isValid(bytes, extension, ctx);
    }
    private boolean isValidData(String val, ConstraintValidatorContext ctx){
        byte[] bytes = null;
        String extension = null;

        if(!StringUtils.isEmpty(val)){
            URL url = null;
            try{
                url = new URL(val);
            }catch (MalformedURLException e){e.printStackTrace();}

            if(url != null){
                if(!isValidSupportAttachType(AttachDataType.URL)) return validFail("Unsupported Data Type : supported -> "+StringUtils.join(supportAttachType, ','), ctx);
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                HttpEntity<String> entity = new HttpEntity<String>(headers);

                ResponseEntity<byte[]> restResp = null;

                try{
                    restResp = restTemplate.exchange(val, HttpMethod.GET, entity, byte[].class);
                    if(restResp != null && restResp.getStatusCode() != null && restResp.getStatusCode().is2xxSuccessful()){
                        bytes = restResp.getBody();
                    }
                }catch (Exception e){e.printStackTrace();}

                if(ObjectUtils.isEmpty(bytes)){
                    return validFail("Not Found URL", ctx);
                }

                int dotLastIndex = (val).lastIndexOf(".");

                MediaType contentType = restResp.getHeaders().getContentType();
                if(contentType != null){
                    extension = contentType.getSubtype().toLowerCase();
                }else{
                    extension = (val).substring(dotLastIndex + 1).toLowerCase();
                }

            }else{
                if(!isValidSupportAttachType(AttachDataType.BASE64)) return validFail("Unsupported Data Type : supported -> "+StringUtils.join(supportAttachType, ','), ctx);

                Pattern p = Pattern.compile("data:(\\S+);base64,");
                Matcher m = p.matcher(val);
                String strMimeType = null;

                if(m.find()){
                    strMimeType = m.group(1);
                    extension = properties.getExtension(strMimeType);
                }else{
                    return validFail("Illegal Base64 Format : data:[FILE MIME TYPE];base64,[DATA ENCODED BASE64]", ctx);
                }

                if(StringUtils.isEmpty(extension)){
                    return validFail("Illegal Base64 Format : data:[FILE MIME TYPE];base64,[DATA ENCODED BASE64]", ctx);
                }

                String tmp = null;
                if(((String) val).indexOf(";base64,") > -1) tmp = (val).split(";base64,")[1];
                if(!Base64.isBase64(tmp)){
                    return validFail("Invalid Base64 Data", ctx);
                }
                try{
                    bytes = Base64.decodeBase64(tmp);
                }catch (Exception e){
                    return validFail("Invalid Base64 Data", ctx);
                }

                if(ObjectUtils.isEmpty(bytes)){
                    return validFail("Invalid Base64 Data", ctx);
                }
            }

            return isValid(bytes, extension, ctx);
        }


        return true;
    }
    private boolean isValid(byte[] bytes, String extension, ConstraintValidatorContext ctx){
        String[] systemSupportFileFormats = systemSupportFileFormat();
        String[] fileFormats = null;

        if(ObjectUtils.isEmpty(supportFileFormat)) fileFormats = systemSupportFileFormats;
        else fileFormats = supportFileFormat;

        String unsupportedFileFormatMessage = extension+" => Unsupported File Format : supported format -> "+StringUtils.join(fileFormats, ',');

        if(StringUtils.isEmpty(extension)){
            return validFail(unsupportedFileFormatMessage, ctx);
        }else{
            if(extension.equalsIgnoreCase("jpeg")) extension = "jpg";

            boolean isSupported = false;
            for(String systemSupportFileFormat : systemSupportFileFormats){
                if(systemSupportFileFormat.equalsIgnoreCase(extension)) {
                    isSupported = true;
                    break;
                }
            }
            if(!isSupported) {
                return validFail(unsupportedFileFormatMessage, ctx);
            }
        }

        if(!StringUtils.isEmpty(maxFileSize)){
            double dMaxFileSize = Util.sizeString2Double(maxFileSize);
            if(bytes.length > dMaxFileSize){
                return validFail(Util.doubleSize2String(bytes.length)+" => Maximum size per file : "+ maxFileSize, ctx);
            }
        }else if(bytes.length > properties.uploadMaxSize()){
            return validFail(Util.doubleSize2String(bytes.length)+" => Maximum size per file : "+ properties.strUploadMaxSize(), ctx);
        }

        boolean isImageFile = properties.isImageFile(extension);
        boolean minImageCheck = (minImageWidth != Integer.MIN_VALUE & minImageHeight != Integer.MIN_VALUE);
        boolean maxImageCheck = (maxImageWidth != Integer.MAX_VALUE & maxImageHeight != Integer.MAX_VALUE);

        if(isImageFile){
            if(minImageCheck | maxImageCheck){
                int imageWidth = 0;
                int imageHeight = 0;
                try {
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                    imageWidth = bufferedImage.getWidth();
                    imageHeight = bufferedImage.getHeight();
                } catch (Exception e) {e.printStackTrace();}

                if(imageWidth == 0 & imageHeight == 0) return validFail("Only image files are supported.", ctx);

                if(minImageCheck){
                    if(imageWidth < minImageWidth | imageHeight < minImageHeight){
                        return validFail(imageWidth+"x"+imageHeight+" => Minimum Image Size : "+ minImageWidth+"x"+minImageHeight, ctx);
                    }
                }
                if(maxImageCheck){
                    if(imageWidth > maxImageWidth | imageHeight > maxImageHeight){
                        return validFail(imageWidth+"x"+imageHeight+" => Maximum Image Size : "+ maxImageWidth+"x"+maxImageHeight, ctx);
                    }
                }
            }
        }
        return true;
    }
    private boolean validFail(String message, ConstraintValidatorContext ctx){
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
    private String[] systemSupportFileFormat(){
        List<SIMCode> codeList = simCodeService.getSIMCodeList(SIMCodeGroup.mimeType);
        List<String> supportFileFormat = new ArrayList<>();
        for(SIMCode code : codeList){ supportFileFormat.add(code.getCodeNm()); }

        return supportFileFormat.toArray(new String[supportFileFormat.size()]);
    }
}
