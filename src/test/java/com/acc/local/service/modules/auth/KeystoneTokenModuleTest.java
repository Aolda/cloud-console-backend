package com.acc.local.service.modules.auth;

import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.security.crypto.KeystonePasswordEncryptor;
import com.acc.local.dto.auth.KeystonePasswordLoginRequest;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class KeystoneTokenModuleTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private KeystonePasswordEncryptor keystonePasswordEncryptor;

    @InjectMocks
    private KeystoneTokenModule keystoneTokenModule;

    @Test
    @DisplayName("사용자 Keystone credential이 있으면 프로젝트 스코프 토큰을 직접 발급한다.")
    void givenUserCredentials_whenIssueScopedTokenByUserCredentials_thenReturnScopedToken() {
        // given
        String userId = "owner-id";
        String projectId = "project-id";
        UserDbExtraEntity user = createUser(userId);
        KeystoneToken scopedToken = KeystoneToken.builder().token("scoped-token").build();
        given(userRepositoryPort.findUserDetailById(userId)).willReturn(Optional.of(user));
        given(keystonePasswordEncryptor.decrypt("encrypted-password")).willReturn("plain-password");
        given(keystoneAPIExternalPort.getScopedTokenByPassword(
                org.mockito.ArgumentMatchers.eq(projectId),
                org.mockito.ArgumentMatchers.any(KeystonePasswordLoginRequest.class)
        )).willReturn(scopedToken);

        // when
        String result = keystoneTokenModule.issueScopedTokenByUserCredentials(userId, projectId);

        // then
        assertThat(result).isEqualTo("scoped-token");
        ArgumentCaptor<KeystonePasswordLoginRequest> requestCaptor =
                ArgumentCaptor.forClass(KeystonePasswordLoginRequest.class);
        then(keystoneAPIExternalPort).should()
                .getScopedTokenByPassword(org.mockito.ArgumentMatchers.eq(projectId), requestCaptor.capture());
        assertThat(requestCaptor.getValue().username()).isEqualTo("owner");
    }

    @Test
    @DisplayName("Keystone credential이 없으면 작업용 토큰을 발급하지 않는다.")
    void givenMissingCredentials_whenIssueScopedTokenByUserCredentials_thenThrowAuthServiceException() {
        // given
        String userId = "owner-id";
        UserDbExtraEntity user = UserDbExtraEntity.builder()
                .userId(userId)
                .build();
        given(userRepositoryPort.findUserDetailById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> keystoneTokenModule.issueScopedTokenByUserCredentials(userId, "project-id"))
                .isInstanceOf(AuthServiceException.class);
    }

    @Test
    @DisplayName("작업용 토큰 폐기에 실패해도 호출자는 예외를 받지 않는다.")
    void givenRevokeFailure_whenRevokeTokenQuietly_thenDoesNotThrowException() {
        // given
        willThrow(new RuntimeException("revoke failed"))
                .given(keystoneAPIExternalPort)
                .revokeToken("scoped-token");

        // when & then
        assertThatCode(() -> keystoneTokenModule.revokeTokenQuietly("scoped-token"))
                .doesNotThrowAnyException();
    }

    private UserDbExtraEntity createUser(String userId) {
        return UserDbExtraEntity.builder()
                .userId(userId)
                .keystoneUsername("owner")
                .keystonePassword("encrypted-password")
                .build();
    }
}
