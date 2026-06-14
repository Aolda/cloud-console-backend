package com.acc.local.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectScopedToken {
    private String projectId;
    private String token;
    private LocalDateTime expiresAt;
}
