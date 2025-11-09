package com.acc.global.exception.google;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum GoogleErrorCode implements ErrorCode {

    CREDENTIALS_LOAD_FAILURE(500, "ACC-GOOGLE-CREDENTIALS-FAILURE", "Google 인증 정보를 로드하는 데 실패했습니다."),
    SHEETS_SERVICE_INIT_FAILURE(500, "ACC-GOOGLE-SERVICE-INIT-FAILURE", "Google Sheets 서비스 초기화에 실패했습니다."),
    PEOPLE_SERVICE_INIT_FAILURE(500, "ACC-PEOPLE-SERVICE-INIT-FAILURE", "Google People 서비스 초기화에 실패했습니다."),
    API_READ_FAILURE(500, "ACC-GOOGLE-API-READ-FAILURE", "Google API로 데이터를 읽어오는 데 실패했습니다."),
    DATA_PARSING_FAILURE(500, "ACC-GOOGLE-DATA-PARSING-FAILURE", "Google Sheets에서 읽어온 데이터의 형식이 올바르지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    GoogleErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
