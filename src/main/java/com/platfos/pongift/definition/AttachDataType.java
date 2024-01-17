package com.platfos.pongift.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 첨부 파일 데이터 타입
 * URL 형태, Base64 인코드 스트링, 바이너리
 */
@Getter
@AllArgsConstructor
public enum AttachDataType {
    URL, BASE64, BINARY;
}
