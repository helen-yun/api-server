package com.platfos.pongift.authorization.annotation;

import com.platfos.pongift.definition.APIRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 권한 Annotation
 * RestController의 Method에 명시
 * 명시된 권한만 접근 가능하도록 설계됨
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface APIPermission {
    /** API 권한 리스트 (기본 : 인증 사용자) **/
    APIRole[] roles() default { APIRole.AUTHENTICATED };
}
