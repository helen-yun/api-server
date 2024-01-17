package com.platfos.pongift.attach.model;

import com.platfos.pongift.validate.annotation.Description;
import lombok.*;

/**
 * 파일 업로드 결과 응답 객체
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFile {
    @Description("파일 업로드 결과 성공 유무")
    private boolean isSuccess;

    @Description("저장된 파일 경로(URL)")
    private String url;

    @Description("원본 파일명")
    private String fileName;
}
