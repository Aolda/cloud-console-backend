package com.acc.global.exception;

/**
 * 모든 에러코드가 구현해야 하는 인터페이스
 *
 * 에러코드 작성 명세: ACC-{도메인명}-{에러명}
 *    - 도메인명 : COMMON, AUTH, COMPUTE, NETWORK, STORAGE 등(대문자)
 *    - 에러명 : 의미를 명확히 드러내는 영문 대문자, 단어 구분은 하이픈(-)
 */
public interface ErrorCode {
    
    int getStatus();
    
    String getCode();
    
    String getMessage();
}
