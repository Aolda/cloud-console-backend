package com.acc.global.handler;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.ErrorResponse;
import com.acc.global.exception.common.CommonErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ACC 커스텀 예외 처리
     * AccBaseException을 상속받는 모든 예외를 처리
     */
    @ExceptionHandler(AccBaseException.class)
    public ResponseEntity<ErrorResponse> handleAccBaseException(AccBaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getCustomMessage();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, message));
    }

    /**
     * 예상하지 못한 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        CommonErrorCode errorCode = CommonErrorCode.INTERNAL_FAILURE;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, ex.getMessage()));
    }
}