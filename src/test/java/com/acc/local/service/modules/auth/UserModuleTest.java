package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.domain.model.auth.User;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.dto.auth.AdminCreateUserRequest;
import com.acc.local.dto.auth.AdminUpdateUserRequest;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.entity.id.UserIdentityId;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.dto.UserDBDto;
import com.acc.local.repository.ports.UserRepositoryPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserModuleTest {

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UserModule userModule;

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

        UserKeystoneDto userKeystoneDto = UserKeystoneDto.builder()
                .id(newUserId)
                .name("hong@ajou.ac.kr")
                .enabled(true)
                .build();

        when(keystoneAPIExternalPort.createUser(eq(adminToken), any()))
                .thenReturn(userKeystoneDto);

        when(userRepositoryPort.saveUserDetail(any()))
                .thenReturn(UserDbExtraEntity.builder()
                        .userId(newUserId)
                        .userName("홍길동")
                        .userPhoneNumber("01012345678")
                        .isAdmin(false)
                        .build());

        when(userRepositoryPort.saveUserIdentity(any()))
                .thenReturn(UserIdentityEntity.builder()
                        .id(new UserIdentityId(newUserId, 0)) // GOOGLE
                        .department("컴퓨터공학과")
                        .studentId("2021123")
                        .userEmail("hong@ajou.ac.kr")
                        .build());

        // when
        String result = userModule.adminCreateUser(request, adminToken);

        // then
        assertEquals(newUserId, result);
        verify(keystoneAPIExternalPort).createUser(eq(adminToken), any());
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

        UserKeystoneDto updatedUserKeystoneDto = UserKeystoneDto.builder()
                .id(userId)
                .name("updated@ajou.ac.kr")
                .enabled(true)
                .build();

        when(keystoneAPIExternalPort.updateUser(eq(userId), eq(token), any()))
                .thenReturn(updatedUserKeystoneDto);

        when(userRepositoryPort.findUserDetailById(userId))
                .thenReturn(Optional.of(
                        UserDbExtraEntity.builder()
                                .userId(userId)
                                .userName("기존")
                                .userPhoneNumber("01000000000")
                                .isAdmin(false)
                                .build()));

        when(userRepositoryPort.findUserAuthById(userId))
                .thenReturn(Optional.of(
                        UserIdentityEntity.builder()
                                .id(new UserIdentityId(userId, 0))
                                .department("old")
                                .studentId("old")
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
    void whenGetUserById_thenReturnUser() throws Exception {

        String userId = "uid-1";
        String token = "admin-token";

        UserKeystoneDto userKeystoneDto = UserKeystoneDto.builder()
                .id("uid-1")
                .name("user@ajou.ac.kr")
                .enabled(true)
                .build();

        when(keystoneAPIExternalPort.getUserDetail(userId, token))
                .thenReturn(userKeystoneDto);

        UserDbExtraEntity userDbExtra = UserDbExtraEntity.builder()
                .userId("uid-1")
                .userName("홍길동")
                .userPhoneNumber("01011112222")
                .isAdmin(false)
                .build();

        UserIdentityEntity userIdentity = UserIdentityEntity.builder()
                .id(new UserIdentityId("uid-1", 0))
                .department("소프트웨어")
                .studentId("2021333")
                .userEmail("user@ajou.ac.kr")
                .build();

        UserDBDto userDBDto = new UserDBDto(userIdentity, userDbExtra);

        when(userRepositoryPort.findUserDBByUserId(userId))
                .thenReturn(Optional.of(userDBDto));

        // when
        User user = userModule.getUserById(userId, token);

        // then
        assertEquals("홍길동", user.getUsername());
        assertEquals("user@ajou.ac.kr", user.getEmail());
        assertEquals("소프트웨어", user.getDepartment());
        assertEquals("2021333", user.getStudentId());
        verify(keystoneAPIExternalPort).getUserDetail(userId, token);
        verify(userRepositoryPort).findUserDBByUserId(userId);
    }


    // ----------------------------------------------------
    // DB UserDetail 없음 예외 테스트 (정합성 불일치)
    // ----------------------------------------------------
    @Test
    @DisplayName("Keystone에는 존재하지만 DB에 없음 → USER_DATA_INCONSISTENCY 발생")
    void whenUserDetailMissing_thenThrowDataInconsistencyException() throws Exception {

        String userId = "uid-x";
        String token = "admin-token";

        UserKeystoneDto userKeystoneDto = UserKeystoneDto.builder()
                .id("uid-x")
                .name("aaa@ajou.ac.kr")
                .enabled(true)
                .build();

        when(keystoneAPIExternalPort.getUserDetail(userId, token))
                .thenReturn(userKeystoneDto);

        when(userRepositoryPort.findUserDBByUserId(userId))
                .thenReturn(Optional.empty());

        // when & then
        AuthServiceException ex =
                assertThrows(AuthServiceException.class,
                        () -> userModule.getUserById(userId, token));

        assertEquals(AuthErrorCode.USER_DATA_INCONSISTENCY, ex.getErrorCode());
    }


    // ----------------------------------------------------
    // 사용자 목록 조회
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 사용자 목록을 조회하여 User 도메인 모델 목록을 반환한다.")
    void whenListUsers_thenReturnPageOfUsers() throws Exception {

        // given
        PageRequest req = new PageRequest();
        req.setMarker(null);
        req.setLimit(10);

        UserKeystoneDto u1 = UserKeystoneDto.builder()
                .id("u1")
                .name("user1@ajou.ac.kr")
                .enabled(true)
                .defaultProjectId("p1")
                .build();
        UserKeystoneDto u2 = UserKeystoneDto.builder()
                .id("u2")
                .name("user2@ajou.ac.kr")
                .enabled(false)
                .build();

        UserListResponse keystoneResponse = UserListResponse.builder()
                .userKeystoneDtos(List.of(u1, u2))
                .nextMarker(null)
                .prevMarker(null)
                .build();

        when(keystoneAPIExternalPort.listUsers(anyString(), any(), anyInt()))
                .thenReturn(keystoneResponse);

        // DB에서 조회될 UserDBDto 목록 설정
        UserDbExtraEntity dbExtra1 = UserDbExtraEntity.builder()
                .userId("u1")
                .userName("홍길동")
                .userPhoneNumber("01011111111")
                .isAdmin(false)
                .isDeleted(false)
                .build();
        UserIdentityEntity identity1 = UserIdentityEntity.builder()
                .id(new UserIdentityId("u1", 0))
                .department("컴퓨터공학과")
                .studentId("2021001")
                .userEmail("user1@ajou.ac.kr")
                .build();

        UserDbExtraEntity dbExtra2 = UserDbExtraEntity.builder()
                .userId("u2")
                .userName("김철수")
                .userPhoneNumber("01022222222")
                .isAdmin(true)
                .isDeleted(false)
                .build();
        UserIdentityEntity identity2 = UserIdentityEntity.builder()
                .id(new UserIdentityId("u2", 1))
                .department("소프트웨어학과")
                .studentId("2021002")
                .userEmail("user2@ajou.ac.kr")
                .build();

        List<UserDBDto> userDBDtos = List.of(
                new UserDBDto(identity1, dbExtra1),
                new UserDBDto(identity2, dbExtra2)
        );

        when(userRepositoryPort.findUserDBsByUserIds(List.of("u1", "u2")))
                .thenReturn(userDBDtos);

        // when
        PageResponse<User> result = userModule.adminListUsers(req, "admin-token");

        // then
        assertEquals(2, result.getContents().size());

        User firstUser = result.getContents().get(0);
        assertEquals("u1", firstUser.getUserId());
        assertEquals("홍길동", firstUser.getUsername());
        assertEquals("user1@ajou.ac.kr", firstUser.getEmail());
        assertEquals("컴퓨터공학과", firstUser.getDepartment());
        assertEquals(true, firstUser.getIsEnabled());

        User secondUser = result.getContents().get(1);
        assertEquals("u2", secondUser.getUserId());
        assertEquals("김철수", secondUser.getUsername());
        assertEquals(true, secondUser.getIsAdmin());

        verify(keystoneAPIExternalPort).listUsers(anyString(), any(), anyInt());
        verify(userRepositoryPort).findUserDBsByUserIds(anyList());
    }

    @Test
    @DisplayName("관리자 사용자 목록은 PageRequest가 null이어도 기본 페이지 값으로 조회한다.")
    void whenListUsersWithNullPage_thenUseDefaultPageRequest() {
        UserKeystoneDto u1 = UserKeystoneDto.builder()
                .id("u1")
                .name("user1@ajou.ac.kr")
                .enabled(true)
                .build();
        UserListResponse keystoneResponse = UserListResponse.builder()
                .userKeystoneDtos(List.of(u1))
                .nextMarker(null)
                .prevMarker(null)
                .build();
        UserDbExtraEntity dbExtra = UserDbExtraEntity.builder()
                .userId("u1")
                .userName("홍길동")
                .userPhoneNumber("01011111111")
                .isAdmin(false)
                .isDeleted(false)
                .build();
        UserIdentityEntity identity = UserIdentityEntity.builder()
                .id(new UserIdentityId("u1", 0))
                .department("컴퓨터공학과")
                .studentId("2021001")
                .userEmail("user1@ajou.ac.kr")
                .build();

        when(keystoneAPIExternalPort.listUsers(eq("admin-token"), isNull(), eq(10)))
                .thenReturn(keystoneResponse);
        when(userRepositoryPort.findUserDBsByUserIds(List.of("u1")))
                .thenReturn(List.of(new UserDBDto(identity, dbExtra)));

        PageResponse<User> result = userModule.adminListUsers(null, "admin-token");

        assertEquals(1, result.getContents().size());
        assertTrue(result.getFirst());
        assertTrue(result.getLast());
        assertNull(result.getNextMarker());
        verify(keystoneAPIExternalPort).listUsers(eq("admin-token"), isNull(), eq(10));
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
                .thenReturn(Optional.ofNullable(UserDbExtraEntity.builder().build()));

        assertDoesNotThrow(() -> userModule.adminDeleteUser(userId, token));

        // verify(keystoneAPIExternalPort).deleteUser(userId, token);
        // verify(userRepositoryPort).deleteUserDetailById(userId);
        // verify(userRepositoryPort).deleteUserAuthById(userId);
    }
}
