package com.acc.local.service.modules.auth;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.security.crypto.KeystonePasswordEncryptor;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeystoneTokenModule {

    private final UserRepositoryPort userRepositoryPort;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final KeystonePasswordEncryptor keystonePasswordEncryptor;

    public KeystoneToken issueUnscopedTokenByUserCredentials(String userId) {
        KeystonePasswordLoginRequest loginRequest = createLoginRequest(userId);
        KeystoneToken token = keystoneAPIExternalPort.getUnscopedToken(loginRequest);
        if (token == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
        }
        return token;
    }

    public String issueScopedTokenByUserCredentials(String userId, String projectId) {
        KeystonePasswordLoginRequest loginRequest = createLoginRequest(userId);
        KeystoneToken token = keystoneAPIExternalPort.getScopedTokenByPassword(projectId, loginRequest);
        if (token == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_TOKEN_GENERATION_FAILED);
        }
        return token.token();
    }

    public void revokeTokenQuietly(String keystoneToken) {
        if (keystoneToken == null || keystoneToken.isBlank()) {
            return;
        }
        try {
            keystoneAPIExternalPort.revokeToken(keystoneToken);
        } catch (Exception e) {
            log.warn("작업용 Keystone 토큰 폐기 실패", e);
        }
    }

    private KeystonePasswordLoginRequest createLoginRequest(String userId) {
        UserDbExtraEntity user = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND));
        validateCredentials(user, userId);
        String password = keystonePasswordEncryptor.decrypt(user.getKeystonePassword());
        return new KeystonePasswordLoginRequest(user.getKeystoneUsername(), password, "Default");
    }

    private void validateCredentials(UserDbExtraEntity user, String userId) {
        if (user.getKeystoneUsername() == null || user.getKeystonePassword() == null) {
            throw new AuthServiceException(
                    AuthErrorCode.USER_NOT_FOUND,
                    "Keystone credentials가 없습니다. userId: " + userId
            );
        }
    }
}
