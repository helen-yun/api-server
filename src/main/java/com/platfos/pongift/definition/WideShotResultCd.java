package com.platfos.pongift.definition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WideShotResultCd {
    // 요청에 대한 응답 코드
    SUCCESS_REQUEST("200", "요청 성공"),
    NO_PERMISSIONS("403", "권한 없음"),
    PARAMETER_ERROR("405", "파라미터 오류"),
    DUPLICATE_TEMPLATE_CODE("504", "템플릿 코드 중복"),
    DUPLICATE_TEMPLATE_NAME("505", "템플릿 이름 중복"),
    TEMPLATE_CONTENT_LENGTH_EXCEED("506", "템플릿 내용이 1000 자 초과"),
    INVALID_PROFILE("507", "유효하지 않은 발신프로필"),
    REQUESTED_DATA_NOT_FOUND("508", "요청한 데이터가 없음"),
    CANNOT_PROCESS_REQUEST("509", "요청을 처리할 수 있는 상태가 아님(ex: 템플릿 검수 요청이 가능한 상태가 아님)"),
    INVALID_BUTTON_FORMAT("510", "템플릿의 버튼 형식이 유효하지 않음"),
    PROFILE_GROUP_NOT_AVAILABLE_TO_ADD("512", "발신프로필 그룹 추가가 가능한 상태가 아님"),
    IMAGE_UPLOAD_FAILED("600", "이미지 업로드 실패"),
    FILE_UPLOAD_FAILED("610", "파일 업로드 실패"),
    INVALID_ATTACHMENT_TYPE("611", "템플릿 문의 첨부파일 형식이 유효하지 않음"),
    PROFILE_REGISTRATION_BLOCKED_801("801", "발신프로필 등록이 차단된 상태"),
    PROFILE_REGISTRATION_BLOCKED_802("802", "발신프로필 등록이 차단된 상태"),
    PROFILE_REGISTRATION_BLOCKED_803("803", "발신프로필 등록이 차단된 상태"),
    PROFILE_REGISTRATION_BLOCKED_804("804", "발신프로필 등록이 차단된 상태"),
    PROFILE_REGISTRATION_BLOCKED_805("805", "발신프로필 등록이 차단된 상태"),
    PROFILE_REGISTRATION_BLOCKED_HUB("811", "발신프로필 등록이 차단된 허브파트너"),
    WIDESHOT_NO_PERMISSIONS("S403", "권한 없음"),
    WIDESHOT_PARAMETER_ERROR("S405", "파라미터가 올바르지 않음"),
    WIDESHOT_UNSUPPORTED_METHOD_TYPES("S406", "지원하지 않는 메서드 형식"),
    WIDESHOT_SERVER_ERROR("S500", "서버 오류"),
    WIDESHOT_NO_DATA("S508", "데이터가 존재하지 않음"),
    
    // 발송 결과 확인 응답 코드
    SEND_SUCCESS("100", "MMSC 전달 성공"),
    SEND_FAILED("999", "발송 실패")
    // todo: 나머지 코드 정의
    ;
    
    
    private final String code;
    private final String reason;
    
    public static WideShotResultCd findByCode(String code){
        if(code == null) return null;
        
        for(WideShotResultCd wideShotResultCd : values()){
            if(code.equals(wideShotResultCd.getCode())){
                return wideShotResultCd;
            }
        }
        return null;
    }
}
