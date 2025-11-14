package com.acc.global.common;

import lombok.Builder;
import lombok.Getter;

/**
 * ResponseEntity<> 형식을 사용하지 못하는 응답 반환을 위한 Response DTO 정의
 *  ex) Spring MVC 앞단에서 실행되어야하는 로직일 때 사용
 * @param <T>
 */
@Getter
@Builder
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return success(message, null);
    }

    public static ApiResponse<Void> error(String errorCode, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
