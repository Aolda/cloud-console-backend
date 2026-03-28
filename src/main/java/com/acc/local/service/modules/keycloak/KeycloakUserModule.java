package com.acc.local.service.modules.keycloak;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.global.security.crypto.KeystonePasswordEncryptor;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.dto.auth.KeycloakIdTokenClaims;
import com.acc.local.dto.auth.KeycloakUserResult;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.entity.id.UserIdentityId;
import com.acc.local.external.dto.keystone.CreateKeystoneUserRequest;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Keycloak OIDC 기반 사용자 조회/등록 모듈.
 *
 * ────────────────────────────────────────────────────────────────
 * [2-way 분기]
 *  Branch 1. keycloakUserId로 DB 조회 → 이미 연결된 사용자 → 일반 로그인
 *  Branch 2. 미존재 → Keystone 신규 사용자 생성 + user_detail + user_auth_detail 저장
 *            └ auth_idp_type 클레임(google/gitlab)으로 AuthType 결정
 *
 * [주의] Keystone API 호출은 트랜잭션 외부 시스템이므로 롤백되지 않는다.
 *        Keystone 생성 성공 후 DB 저장 실패 시 Keystone 고아(orphan) 사용자가 생길 수 있다.
 *        운영 환경에서는 보상 트랜잭션(Keystone 사용자 삭제) 처리를 추가 검토할 것.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserModule {

    private final UserRepositoryPort userRepositoryPort;
    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final KeystonePasswordEncryptor keystonePasswordEncryptor;
    private final AuthModule authModule;

    /**
     * Keycloak 로그인 시 사용자를 조회하거나 등록한다.
     *
     * @param claims    Keycloak ID Token에서 추출한 클레임
     * @param departDto 학적 정보 (DB resolve 완료)
     * @return 이후 Keystone 토큰 발급 + SessionData 구성에 필요한 결과
     */
    @Transactional
    public KeycloakUserResult findOrRegisterKeycloakUser(KeycloakIdTokenClaims claims, UserDepartDto departDto) {

        // ── Branch 1: keycloakUserId가 이미 user_detail에 연결된 사용자 ──────────────
        Optional<UserDbExtraEntity> linkedUser =
                userRepositoryPort.findUserDetailByKeycloakUserId(claims.subject());

        if (linkedUser.isPresent()) {
            UserDbExtraEntity entity = linkedUser.get();
            String plainPassword = keystonePasswordEncryptor.decrypt(entity.getKeystonePassword());
            log.debug("Keycloak 로그인 - 기존 연결 사용자: keystoneUserId={}", entity.getUserId());
            return new KeycloakUserResult(
                    entity.getUserId(),
                    entity.getKeystoneUsername(),
                    plainPassword,
                    entity.getUserName()
            );
        }

        // ── Branch 2: 신규 사용자 → Keystone 사용자 생성 + DB 저장 ──────────────────
        if (departDto == null) {
            throw new AuthServiceException(AuthErrorCode.USER_NOT_FOUND,
                    "관리자 계정 사전 등록이 필요합니다.");
        }
        return registerNewKeycloakUser(claims, departDto);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Branch 2: 신규 사용자 등록
    // 기존에 어떤 방식으로도 가입하지 않은 사용자가 Keycloak으로 최초 로그인하는 경우.
    //
    // [phoneNumber] Keycloak이 전화번호를 제공하지 않으므로 빈 문자열로 초기화.
    //               사용자가 이후 프로필 수정을 통해 채워야 한다.
    // ─────────────────────────────────────────────────────────────────────────────
    private KeycloakUserResult registerNewKeycloakUser(KeycloakIdTokenClaims claims, UserDepartDto departDto) {
        String adminToken = authModule.issueSystemAdminToken(null);

        String newPassword = generateSecurePassword();
        CreateKeystoneUserRequest createRequest = CreateKeystoneUserRequest.builder()
                .email(claims.email())
                .password(newPassword)
                .isEnable(true)
                .build();
        UserKeystoneDto createdUser = keystoneAPIExternalPort.createUser(adminToken, createRequest);

        String keystoneUserId = createdUser.id();
        String keystoneUsername = createdUser.name();

        // user_detail 저장
        UserDbExtraEntity userDbExtraEntity = UserDbExtraEntity.builder()
                .userId(keystoneUserId)
                .userName(claims.preferredUsername())
                .userPhoneNumber("")   // Keycloak은 전화번호를 제공하지 않음. 추후 사용자 업데이트 필요.
                .isAdmin(false)
                .keycloakUserId(claims.subject())
                .keystoneUsername(keystoneUsername)
                .keystonePassword(keystonePasswordEncryptor.encrypt(newPassword))
                .build();
        userRepositoryPort.saveUserDetail(userDbExtraEntity);

        // user_auth_detail 저장 (auth_idp_type 클레임으로 AuthType 결정)
        UserIdentityEntity userIdentityEntity = UserIdentityEntity.builder()
                .id(new UserIdentityId(keystoneUserId, AuthType.fromIdpType(claims.authIdpType()).getCode()))
                .department(departDto.department())
                .studentId(claims.ajouStudentId())
                .userEmail(claims.email())
                .build();
        userRepositoryPort.saveUserIdentity(userIdentityEntity);

        log.info("신규 Keycloak 사용자 등록 완료 - keystoneUserId={}, keycloakUserId={}",
                keystoneUserId, claims.subject());

        return new KeycloakUserResult(keystoneUserId, keystoneUsername, newPassword, claims.preferredUsername());
    }

    /**
     * 이미 연결된 관리자 계정 여부 확인 (학적 검증 우회 판단용).
     */
    public boolean isLinkedAdmin(String keycloakUserId) {
        return userRepositoryPort.findUserDetailByKeycloakUserId(keycloakUserId)
                .map(u -> Boolean.TRUE.equals(u.getIsAdmin()))
                .orElse(false);
    }

    /**
     * Keystone 패스워드로 사용할 랜덤 문자열 생성.
     * UUID 기반 36자 (영문+숫자+하이픈). Keystone 기본 패스워드 정책 충족.
     */
    private String generateSecurePassword() {
        return UUID.randomUUID().toString();
    }
}
