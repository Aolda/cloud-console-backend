package com.acc.local.service.modules.session;

import com.acc.local.domain.model.session.KeystoneTokens;
import com.acc.local.domain.model.session.ProjectScopedToken;
import com.acc.local.domain.model.session.SessionData;
import com.acc.local.dto.auth.KeystoneToken;
import com.acc.local.external.ports.KeycloakOidcExternalPort;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.SessionRepositoryPort;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SessionModuleTest {

    @Mock
    private SessionRepositoryPort sessionRepositoryPort;

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private KeycloakOidcExternalPort keycloakOidcExternalPort;

    @Mock
    private KeystoneTokenModule keystoneTokenModule;

    @InjectMocks
    private SessionModule sessionModule;

    @Test
    @DisplayName("프로젝트 스코프 토큰을 발급하면 로그아웃 폐기를 위해 세션에 추적한다.")
    void givenSession_whenGetKeystoneScopedToken_thenTrackScopedTokenInSession() {
        // given
        String sessionId = "session-id";
        String projectId = "project-id";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        SessionData sessionData = SessionData.builder()
                .keystoneTokens(KeystoneTokens.builder()
                        .unscopedToken("unscoped-token")
                        .expiresAt(expiresAt)
                        .build())
                .build();
        KeystoneToken scopedToken = KeystoneToken.builder()
                .token("scoped-token")
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .build();

        given(sessionRepositoryPort.findById(sessionId)).willReturn(Optional.of(sessionData));
        given(keystoneAPIExternalPort.getScopedToken(projectId, "unscoped-token")).willReturn(scopedToken);

        // when
        String result = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        // then
        assertThat(result).isEqualTo("scoped-token");
        ArgumentCaptor<KeystoneTokens> tokensCaptor = ArgumentCaptor.forClass(KeystoneTokens.class);
        then(sessionRepositoryPort).should().updateField(eq(sessionId), eq("keystoneTokens"), tokensCaptor.capture());

        KeystoneTokens updatedTokens = tokensCaptor.getValue();
        assertThat(updatedTokens.getUnscopedToken()).isEqualTo("unscoped-token");
        assertThat(updatedTokens.getScopedToken()).isEqualTo("scoped-token");
        assertThat(updatedTokens.getScopedTokens()).hasSize(1);
        assertThat(updatedTokens.getScopedTokens().get(0).getProjectId()).isEqualTo(projectId);
        assertThat(updatedTokens.getScopedTokens().get(0).getToken()).isEqualTo("scoped-token");
        assertThat(updatedTokens.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("기존 프로젝트 스코프 토큰 추적 목록이 있으면 새 토큰을 누적한다.")
    void givenTrackedScopedToken_whenGetKeystoneScopedToken_thenAppendScopedToken() {
        // given
        String sessionId = "session-id";
        String projectId = "project-2";
        SessionData sessionData = SessionData.builder()
                .keystoneTokens(KeystoneTokens.builder()
                        .unscopedToken("unscoped-token")
                        .scopedTokens(java.util.List.of(
                                ProjectScopedToken.builder()
                                        .projectId("project-1")
                                        .token("scoped-token-1")
                                        .build()))
                        .expiresAt(LocalDateTime.now().plusMinutes(30))
                        .build())
                .build();
        KeystoneToken scopedToken = KeystoneToken.builder().token("scoped-token-2").build();

        given(sessionRepositoryPort.findById(sessionId)).willReturn(Optional.of(sessionData));
        given(keystoneAPIExternalPort.getScopedToken(projectId, "unscoped-token")).willReturn(scopedToken);

        // when
        sessionModule.getKeystoneScopedToken(sessionId, projectId);

        // then
        ArgumentCaptor<KeystoneTokens> tokensCaptor = ArgumentCaptor.forClass(KeystoneTokens.class);
        then(sessionRepositoryPort).should().updateField(eq(sessionId), eq("keystoneTokens"), tokensCaptor.capture());
        assertThat(tokensCaptor.getValue().getScopedTokens())
                .extracting("token")
                .containsExactly("scoped-token-1", "scoped-token-2");
    }

    @Test
    @DisplayName("Keystone unscoped token을 재발급해도 기존 프로젝트 스코프 토큰 추적 목록은 유지한다.")
    void givenTrackedScopedToken_whenReissueKeystoneUnscopedToken_thenPreserveScopedTokens() {
        // given
        String sessionId = "session-id";
        SessionData sessionData = SessionData.builder()
                .keystoneUserId("user-id")
                .keystoneTokens(KeystoneTokens.builder()
                        .unscopedToken("expired-unscoped-token")
                        .scopedTokens(java.util.List.of(
                                ProjectScopedToken.builder()
                                        .projectId("project-1")
                                        .token("scoped-token-1")
                                        .build()))
                        .expiresAt(LocalDateTime.now().minusMinutes(1))
                        .build())
                .build();
        KeystoneToken newUnscopedToken = KeystoneToken.builder()
                .token("new-unscoped-token")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        given(sessionRepositoryPort.findById(sessionId)).willReturn(Optional.of(sessionData));
        given(keystoneTokenModule.issueUnscopedTokenByUserCredentials("user-id")).willReturn(newUnscopedToken);

        // when
        String result = sessionModule.getKeystoneUnscopedToken(sessionId);

        // then
        assertThat(result).isEqualTo("new-unscoped-token");
        ArgumentCaptor<KeystoneTokens> tokensCaptor = ArgumentCaptor.forClass(KeystoneTokens.class);
        then(sessionRepositoryPort).should().updateField(eq(sessionId), eq("keystoneTokens"), tokensCaptor.capture());
        assertThat(tokensCaptor.getValue().getUnscopedToken()).isEqualTo("new-unscoped-token");
        assertThat(tokensCaptor.getValue().getScopedTokens())
                .extracting("token")
                .containsExactly("scoped-token-1");
    }
}
