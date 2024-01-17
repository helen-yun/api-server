package com.platfos.pongift.attach.model;

import com.platfos.pongift.definition.AttachDataType;
import com.platfos.pongift.data.sim.model.SIMCode;
import lombok.*;

/**
 * 첨부 파일 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachFile {
    /** 파일 데이터(스트링) **/
    private String data;
    /** 파일 데이터(바이너리) **/
    private byte[] bytes;
    /** 첨부 파일 데이터 타입 **/
    private AttachDataType dataType;
    /** 파일 확장자 **/
    private String extension;
    /** 파일형태 **/
    private SIMCode mimeType;
    /** 저장된 파일 경로(URL) **/
    private String filePath;
    /** 저장된 파일 이름(URL) **/
    private String fileName;
    /** 원본 파일명 **/
    private String originalFileName;
    /** 저장된 물리적 파일 경로 **/
    private String physicalFilePath;
}
