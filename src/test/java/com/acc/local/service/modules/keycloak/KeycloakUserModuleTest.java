package com.acc.local.service.modules.keycloak;

import com.acc.global.security.crypto.KeystonePasswordEncryptor;
import com.acc.local.dto.auth.KeycloakIdTokenClaims;
import com.acc.local.dto.auth.KeycloakUserResult;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakUserModuleTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private KeystonePasswordEncryptor keystonePasswordEncryptor;

    @Mock
    private AuthModule authModule;

    @InjectMocks
    private KeycloakUserModule keycloakUserModule;

    @Test
    void linkedUserNameIsSyncedFromKeycloakDisplayName() {
        UserDbExtraEntity existingUser = UserDbExtraEntity.builder()
                .userId("keystone-user-id")
                .userName("hando1220@ajou.ac.kr")
                .userPhoneNumber("010-0000-0000")
                .isAdmin(false)
                .keycloakUserId("keycloak-user-id")
                .keystoneUsername("hando1220")
                .keystonePassword("encrypted-password")
                .build();
        KeycloakIdTokenClaims claims = new KeycloakIdTokenClaims(
                "keycloak-user-id",
                "hyeonje@example.com",
                "hyeonje",
                "현제 이",
                "현제",
                "이",
                "소프트웨어및컴퓨터공학전공",
                "SS0001(학생(학부))",
                "1",
                "202012345",
                "010-0000-0000",
                "google",
                List.of()
        );

        when(userRepositoryPort.findUserDetailByKeycloakUserId("keycloak-user-id"))
                .thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.saveUserDetail(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(keystonePasswordEncryptor.decrypt("encrypted-password")).thenReturn("plain-password");

        KeycloakUserResult result = keycloakUserModule.findOrRegisterKeycloakUser(claims, departDto(), false);

        ArgumentCaptor<UserDbExtraEntity> captor = ArgumentCaptor.forClass(UserDbExtraEntity.class);
        verify(userRepositoryPort).saveUserDetail(captor.capture());
        assertEquals("현제 이", captor.getValue().getUserName());
        assertEquals("현제 이", result.userName());
    }

    private UserDepartDto departDto() {
        return new UserDepartDto(
                "소프트웨어및컴퓨터공학전공",
                "정보통신대학",
                "소프트웨어및컴퓨터공학전공",
                1,
                null,
                null
        );
    }
}
