package com.acc.local.service.modules.keycloak;

import com.acc.global.properties.KeycloakProperties;
import com.acc.global.security.keycloak.KeycloakIdTokenParser;
import com.acc.local.domain.model.session.KeycloakTokens;
import com.acc.local.domain.model.session.KeystoneTokens;
import com.acc.local.domain.model.session.ProjectScopedToken;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.external.ports.KeycloakOidcExternalPort;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.auth.AjouUnivModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthModuleTest {

    @Mock
    private KeycloakOidcExternalPort keycloakOidcExternalPort;

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private SessionRepositoryPort sessionRepositoryPort;

    @Mock
    private KeycloakUserModule keycloakUserModule;

    @Mock
    private AjouUnivModule ajouUnivModule;

    @Mock
    private KeystoneTokenModule keystoneTokenModule;

    @Mock
    private KeycloakIdTokenParser keycloakIdTokenParser;

    @Mock
    private KeycloakProperties keycloakProperties;

    @InjectMocks
    private KeycloakAuthModule keycloakAuthModule;

    @Test
    @DisplayName("로그아웃 시 Keycloak refresh token과 세션에 추적된 Keystone 토큰을 모두 폐기한다.")
    void givenSessionTokens_whenLogout_thenRevokeKeycloakAndKeystoneTokens() {
        // given
        String sessionId = "session-id";
        SessionData sessionData = SessionData.builder()
                .keycloakTokens(KeycloakTokens.builder()
                        .refreshToken("refresh-token")
                        .build())
                .keystoneTokens(KeystoneTokens.builder()
                        .unscopedToken("unscoped-token")
                        .scopedToken("latest-scoped-token")
                        .scopedTokens(List.of(
                                ProjectScopedToken.builder().projectId("project-1").token("scoped-token-1").build(),
                                ProjectScopedToken.builder().projectId("project-2").token("scoped-token-2").build()))
                        .build())
                .build();
        given(sessionRepositoryPort.findById(sessionId)).willReturn(Optional.of(sessionData));

        // when
        keycloakAuthModule.logout(sessionId);

        // then
        then(keycloakOidcExternalPort).should().revokeToken("refresh-token");
        then(keystoneTokenModule).should().revokeTokenQuietly("unscoped-token");
        then(keystoneTokenModule).should().revokeTokenQuietly("latest-scoped-token");
        then(keystoneTokenModule).should().revokeTokenQuietly("scoped-token-1");
        then(keystoneTokenModule).should().revokeTokenQuietly("scoped-token-2");
        then(sessionRepositoryPort).should().deleteById(sessionId);
    }
}
