package com.acc.local.service.modules.keypair;

import com.acc.local.entity.KeypairEntity;
import com.acc.local.entity.ProjectEntity;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.ports.KeypairExternalPort;
import com.acc.local.repository.ports.KeypairRepositoryPort;
import com.acc.local.repository.ports.ProjectRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.KeystoneTokenModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class KeypairModuleTest {

    @Mock
    private KeypairRepositoryPort keypairRepositoryPort;

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @Mock
    private KeypairExternalPort keypairExternalPort;

    @Mock
    private AuthModule authModule;

    @Mock
    private KeystoneTokenModule keystoneTokenModule;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private KeypairModule keypairModule;

    @Test
    @DisplayName("레거시 키페어 삭제에서 발급한 소유자 프로젝트 스코프 토큰은 사용 후 즉시 폐기한다.")
    void givenKeypair_whenDeleteKeypair_thenRevokeOwnerScopedToken() {
        // given
        String keypairId = "keypair-id";
        String projectId = "project-id";
        UserDbExtraEntity owner = UserDbExtraEntity.builder()
                .userId("owner-id")
                .build();
        KeypairEntity keypair = KeypairEntity.builder()
                .keypairId(keypairId)
                .keypairName("keypair-name")
                .user(owner)
                .project(ProjectEntity.builder().projectId(projectId).build())
                .build();
        given(keypairRepositoryPort.findById(any())).willReturn(Optional.of(keypair));
        given(authModule.issueProjectScopeToken(projectId, "owner-id")).willReturn("owner-scoped-token");

        // when
        keypairModule.deleteKeypair(keypairId, "requester-id", projectId);

        // then
        then(keypairExternalPort).should().deleteKeypair("owner-scoped-token", "keypair-name");
        then(keystoneTokenModule).should().revokeTokenQuietly("owner-scoped-token");
    }
}
