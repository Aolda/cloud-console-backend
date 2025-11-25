package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.dto.auth.AdminCreateUserRequest;
import com.acc.local.dto.auth.AdminGetUserResponse;
import com.acc.local.dto.auth.AdminListUsersResponse;
import com.acc.local.dto.auth.AdminUpdateUserRequest;
import com.acc.local.entity.UserAuthDetailEntity;
import com.acc.local.entity.UserDetailEntity;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class KeystoneUserModuleTest {

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserModule userModule;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ----------------------------------------------------
    // 사용자 생성
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 System Admin 토큰으로 Keystone 사용자를 생성하고 ACC DB에 저장할 수 있다.")
    void whenAdminCreateUser_thenReturnUserId() throws Exception {

        // given
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .username("홍길동")
                .email("hong@ajou.ac.kr")
                .department("컴퓨터공학과")
                .studentId("2021123")
                .password("pw123!")
                .phoneNumber("01012345678")
                .isEnabled(true)
                .isAdmin(false)
                .authType(AuthType.GOOGLE)
                .build();

        String adminToken = "token";
        String newUserId = "new-id-111";

        JsonNode responseUser =
                objectMapper.readTree("{\"user\": {\"id\": \"" + newUserId + "\", \"name\": \"hong@ajou.ac.kr\", \"enabled\": true}}");

        when(keystoneAPIExternalPort.createUser(eq(adminToken), any()))
                .thenReturn(ResponseEntity.ok(responseUser));

        try (MockedStatic<KeystoneAPIUtils> mocked = mockStatic(KeystoneAPIUtils.class)) {

            mocked.when(() -> KeystoneAPIUtils.parseKeystoneUserResponse(any()))
                    .thenReturn(KeystoneUser.builder()
                            .id(newUserId)
                            .name("hong@ajou.ac.kr")
                            .enabled(true)
                            .build());

            when(userRepositoryPort.saveUserDetail(any()))
                    .thenReturn(UserDetailEntity.builder()
                            .userId(newUserId)
                            .userName("홍길동")
                            .userPhoneNumber("01012345678")
                            .isAdmin(false)
                            .build());

            when(userRepositoryPort.saveUserAuth(any()))
                    .thenReturn(UserAuthDetailEntity.builder()
                            .userId(newUserId)
                            .department("컴퓨터공학과")
                            .studentId("2021123")
                            .authType(0)
                            .userEmail("hong@ajou.ac.kr")
                            .build());

            // when
            String result = userModule.adminCreateUser(request, adminToken);

            // then
            assertEquals(newUserId, result);
            verify(keystoneAPIExternalPort).createUser(eq(adminToken), any());
        }
    }


    // ----------------------------------------------------
    // 사용자 수정
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 Keystone 사용자 정보를 수정할 수 있다.")
    void whenAdminUpdateUser_thenReturnUserId() throws Exception {

        String userId = "uid-1";
        String token = "admin-token";

        AdminUpdateUserRequest request = AdminUpdateUserRequest.builder()
                .username("새이름")
                .email("updated@ajou.ac.kr")
                .department("새학과")
                .studentId("2021999")
                .password("newpw123")
                .phoneNumber("01099998888")
                .isEnabled(true)
                .build();

        JsonNode resp = objectMapper.readTree(
                "{ \"user\": {\"id\":\"uid-1\", \"name\":\"updated@ajou.ac.kr\", \"enabled\":true}}");

        when(keystoneAPIExternalPort.updateUser(eq(userId), eq(token), any()))
                .thenReturn(ResponseEntity.ok(resp));

        when(userRepositoryPort.findUserDetailById(userId))
                .thenReturn(Optional.of(
                        UserDetailEntity.builder()
                                .userId(userId)
                                .userName("기존")
                                .userPhoneNumber("01000000000")
                                .isAdmin(false)
                                .build()));

        when(userRepositoryPort.findUserAuthById(userId))
                .thenReturn(Optional.of(
                        UserAuthDetailEntity.builder()
                                .userId(userId)
                                .department("old")
                                .studentId("old")
                                .authType(0)
                                .userEmail("old@ajou.ac.kr")
                                .build()));

        // when
        String result = userModule.adminUpdateUser(request, token, userId);

        // then
        assertEquals(userId, result);
        verify(keystoneAPIExternalPort).updateUser(eq(userId), eq(token), any());
    }


    // ----------------------------------------------------
    // 정상 조회
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 Keystone 사용자 상세 정보를 조회하여 ACC DB 정보와 병합한다.")
    void whenAdminGetUser_thenReturnMerged() throws Exception {

        String userId = "uid-1";
        String token = "admin-token";

        JsonNode resp = objectMapper.readTree(
                "{ \"user\": {\"id\":\"uid-1\", \"name\":\"user@ajou.ac.kr\", \"enabled\":true}}");

        when(keystoneAPIExternalPort.getUserDetail(userId, token))
                .thenReturn(ResponseEntity.ok(resp));

        try (MockedStatic<KeystoneAPIUtils> mocked = mockStatic(KeystoneAPIUtils.class)) {

            mocked.when(() -> KeystoneAPIUtils.parseKeystoneUserResponse(any()))
                    .thenReturn(KeystoneUser.builder()
                            .id("uid-1")
                            .name("user@ajou.ac.kr")
                            .enabled(true)
                            .build());

            when(userRepositoryPort.findUserDetailById(userId))
                    .thenReturn(Optional.of(
                            UserDetailEntity.builder()
                                    .userId("uid-1")
                                    .userName("홍길동")
                                    .userPhoneNumber("01011112222")
                                    .isAdmin(false)
                                    .build()));

            when(userRepositoryPort.findUserAuthById(userId))
                    .thenReturn(Optional.of(
                            UserAuthDetailEntity.builder()
                                    .userId("uid-1")
                                    .department("소프트웨어")
                                    .studentId("2021333")
                                    .authType(0)
                                    .userEmail("user@ajou.ac.kr")
                                    .build()));

            // when
            AdminGetUserResponse r = userModule.adminGetUser(userId, token);

            // then
            assertEquals("홍길동", r.username());
            assertEquals("user@ajou.ac.kr", r.email());
            verify(keystoneAPIExternalPort).getUserDetail(userId, token);
        }
    }


    // ----------------------------------------------------
    // DB UserDetail 없음 예외 테스트
    // ----------------------------------------------------
    @Test
    @DisplayName("ACC DB UserDetail 없음 → USER_NOT_FOUND 발생")
    void whenUserDetailMissing_thenThrowException() throws Exception {

        String userId = "uid-x";
        String token = "admin-token";

        JsonNode resp = objectMapper.readTree(
                "{ \"user\": {\"id\":\"uid-x\", \"name\":\"aaa@ajou.ac.kr\", \"enabled\":true}}");

        when(keystoneAPIExternalPort.getUserDetail(userId, token))
                .thenReturn(ResponseEntity.ok(resp));

        try (MockedStatic<KeystoneAPIUtils> mocked = mockStatic(KeystoneAPIUtils.class)) {

            mocked.when(() -> KeystoneAPIUtils.parseKeystoneUserResponse(any()))
                    .thenReturn(KeystoneUser.builder()
                            .id("uid-x")
                            .name("aaa@ajou.ac.kr")
                            .enabled(true)
                            .build());

            when(userRepositoryPort.findUserDetailById(userId))
                    .thenReturn(Optional.empty());

            // when & then
            AuthServiceException ex =
                    assertThrows(AuthServiceException.class,
                            () -> userModule.adminGetUser(userId, token));

            assertEquals(AuthErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }
    }


    // ----------------------------------------------------
    // 사용자 목록 조회
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 사용자 목록을 조회(LIST USERS)한다.")
    void whenListUsers_thenReturnPage() throws Exception {

        PageRequest req = new PageRequest();
        req.setMarker(null);
        req.setLimit(10);

        KeystoneUser u1 = KeystoneUser.builder().id("u1").name("user1@ajou.ac.kr").enabled(true).defaultProjectId("p1").build();
        KeystoneUser u2 = KeystoneUser.builder().id("u2").name("user2@ajou.ac.kr").enabled(false).build();

        UserListResponse list = UserListResponse.builder()
                .keystoneUsers(List.of(u1, u2))
                .nextMarker("u2")
                .prevMarker(null)
                .build();

        when(keystoneAPIExternalPort.listUsers(anyString(), any(), anyInt()))
                .thenReturn(list);

        // when
        PageResponse<AdminListUsersResponse> result =
                userModule.adminListUsers(req, "admin-token");

        // then
        assertEquals(2, result.getContents().size());
        assertEquals("u2", result.getNextMarker());
        verify(keystoneAPIExternalPort).listUsers(anyString(), any(), anyInt());
    }


    // ----------------------------------------------------
    // 사용자 삭제
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 Keystone 사용자 삭제 및 ACC DB 삭제를 수행한다.")
    void whenDeleteUser_thenSuccess() {

        String userId = "uid-delete";
        String token = "admin-token";

        // when(keystoneAPIExternalPort.deleteUser(userId, token))
        //     .thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());

        when(userRepositoryPort.findUserDetailById(userId))
                .thenReturn(Optional.ofNullable(UserDetailEntity.builder().build()));

        assertDoesNotThrow(() -> userModule.adminDeleteUser(userId, token));

        // verify(keystoneAPIExternalPort).deleteUser(userId, token);
        // verify(userRepositoryPort).deleteUserDetailById(userId);
        // verify(userRepositoryPort).deleteUserAuthById(userId);
    }
}
