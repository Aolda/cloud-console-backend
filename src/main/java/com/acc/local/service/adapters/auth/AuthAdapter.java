package com.acc.local.service.adapters.auth;
import com.acc.local.domain.enums.ProjectPermission;
import com.acc.local.domain.model.User;
import com.acc.local.dto.auth.CreateUserRequest;
import com.acc.local.dto.auth.CreateUserResponse;
import com.acc.local.dto.auth.GetUserResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.ports.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
    private final AuthModule authModule;

    @Override
    public String issueKeystoneToken() {
        return authModule.issueKeystoneToken();
    }

    // keycloak 로그인 이후 redirect URL 엔드포인트에서 사용될 메서드
    @Override
    public String authenticateAndGenerateJwt(String keycloakToken) {
        return authModule.authenticateAndGenerateJwt(keycloakToken);
    }

    @Override
    public boolean validateJwt(String jwtToken) {
        return authModule.validateJwtToken(jwtToken);
    }

    @Override
    public void invalidateUserTokens(String userId) {
        authModule.invalidateUserTokens(userId);
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest , String userId) {

        // TODO: userid 를 통해, 요청을 보낸 사람이 Root인지 권한 확인

        User user = User.builder()
                .keycloakUserId(null)
                .email(createUserRequest.getUserEmail())
                .userPassword(createUserRequest.getUserPassword())
                .userName(createUserRequest.getUserName())
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .enabled(true)
                .build();

        return authModule.createUser(user, userId);
    }

    @Override
    public GetUserResponse getUserDetail(String targetUserId, String requesterId) {
        return authModule.getUserDetail(targetUserId, requesterId);
    }

    @Override
    public ProjectPermission getProjectPermission(String projectId, String userid) {
        return authModule.getProjectPermission(projectId, userid);
    }
}
