package com.platfos.pongift.attach.model;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Request Parameter InputStream 리소스 객체(From org.springframework.web.multipart.MultipartFile)
 */
public class MultipartInputStreamFileResource extends InputStreamResource {
    /** 파일명 **/
    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1;
    }
}