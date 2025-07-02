package com.acc.server.local.dto.external.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeystoneTokenResponse {
    private String xSubjectToken; // 헤더 값
    private KeystoneTokenResponseBody body; // 바디 DTO
}
