package com.acc.local.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeystoneTokens {
    private String unscopedToken;
    private String scopedToken;
    private LocalDateTime expiresAt;
}
