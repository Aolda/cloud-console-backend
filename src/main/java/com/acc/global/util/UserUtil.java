package com.acc.global.util;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;

public class UserUtil {

    private static final String AJOUS_EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@ajou\\.ac\\.kr$";
    private static final String STUDENT_ID_PATTERN = "^\\d{9}$";
    private static final String PHONE_PATTERN = "^01[0-9]-\\d{4}-\\d{4}$";

    /**
     * @param email 사용자 이메일
     * @throws IllegalArgumentException 이메일 형식 오류
     */
    public static void validateEmailFormat(String email) {
        if (email == null || email.isBlank()) {
           return ;
        }
        if (!email.matches(AJOUS_EMAIL_PATTERN)) {
            throw new AuthServiceException(AuthErrorCode.INVALID_REQUEST_PARAMETER, "유효한 이메일 형식이 아닙니다.(@ajou.ac.kr)");
        }
    }


    /**
     * @param studentId 9자리 학번
     * @throws IllegalArgumentException 학번 형식 오류
     */
    public static void validateStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return ;
        }
        if (!studentId.matches(STUDENT_ID_PATTERN)) {
            throw new AuthServiceException(AuthErrorCode.INVALID_REQUEST_PARAMETER, "학번은 총 9개의 숫자로 구성됩니다.");
        }
    }

    /**
     * @param phoneNumber 01x-xxxx-xxxx
     * @throws IllegalArgumentException 전화번호 형식 오류
     */
    public static void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return ;
        }
        if (!phoneNumber.matches(PHONE_PATTERN)) {
            throw new AuthServiceException(AuthErrorCode.INVALID_REQUEST_PARAMETER, "학번은 총 7개의 숫자로 구성됩니다.");
        }
    }
}

