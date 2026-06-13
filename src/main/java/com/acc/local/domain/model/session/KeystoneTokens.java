package com.acc.local.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeystoneTokens {
    private String unscopedToken;
    private String scopedToken;
    private List<ProjectScopedToken> scopedTokens;
    private LocalDateTime expiresAt;
}
