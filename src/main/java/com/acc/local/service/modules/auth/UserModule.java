package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.domain.model.auth.RoleAssignmentListResponse;
import com.acc.local.domain.model.auth.User;
import com.acc.local.domain.model.auth.UserListResponse;
import com.acc.local.repository.dto.UserDBDto;
import com.acc.local.dto.auth.AdminCreateUserRequest;
import com.acc.local.dto.auth.AdminUpdateUserRequest;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.external.dto.keystone.CreateKeystoneUserRequest;
import com.acc.local.external.dto.keystone.UpdateKeystoneUserRequest;
import com.acc.local.external.modules.keystone.KeystoneAPIUtils;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserModule {

    private final KeystoneAPIExternalPort keystoneAPIExternalPort;
    private final UserRepositoryPort userRepositoryPort;
    /**
     * 관리자 사용자 생성 처리
     * System Admin 권한으로 Keystone 사용자 생성 후 ACC DB에 저장
     */
    @Transactional
    public String adminCreateUser(AdminCreateUserRequest request, String adminToken) {
        // 1. Keystone 사용자 생성 요청 생성
        CreateKeystoneUserRequest newUserKeystoneDto = CreateKeystoneUserRequest.builder()
                .email(request.email())
                .password(request.password())
                .isEnable(request.isEnabled())
                .build();
        UserKeystoneDto createdUserKeystoneDto = keystoneAPIExternalPort.createUser(adminToken, newUserKeystoneDto);
        String userIdentityId = createdUserKeystoneDto.id();

        // 2. UserDbExtra Entity 생성 및 저장
        UserDbExtraEntity userDbExtraEntity = UserDbExtraEntity.builder()
                .userId(userIdentityId)
                .userName(request.username())
                .userPhoneNumber(request.phoneNumber())
                .isAdmin(request.isAdmin())
                .build();
        userRepositoryPort.saveUserDetail(userDbExtraEntity);

        // 3. UserIdentity Entity 생성 및 저장
        UserIdentityEntity userIdentityEntity = UserIdentityEntity.builder()
                .userId(userIdentityId)
                .department(request.department())
                .studentId(request.studentId())
                .authType(request.authType().getCode())
                .userEmail(request.email())
                .build();
        userRepositoryPort.saveUserIdentity(userIdentityEntity);

        return userIdentityId;
    }

    /**
     * 관리자 사용자 수정 처리
     * System Admin 권한으로 Keystone 사용자 수정 및 ACC DB 업데이트
     */
    @Transactional
    public String adminUpdateUser(AdminUpdateUserRequest request, String adminToken, String userId) {

        // 1. Keystone 사용자 업데이트
        UpdateKeystoneUserRequest updateRequestDto = UpdateKeystoneUserRequest.builder()
                .email(Optional.of(request.email()).orElse(null))
                .password(request.password())
                .isEnable(request.isEnabled())
                .build();
        keystoneAPIExternalPort.updateUser(userId, adminToken, updateRequestDto);

        // 2. ACC DB 업데이트
        // UserDetailEntity 업데이트
        if (request.username() != null || request.phoneNumber() != null) {
            UserDbExtraEntity userDbExtraEntity = userRepositoryPort.findUserDetailById(userId)
                    .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

            UserDbExtraEntity updatedEntity = userDbExtraEntity.toBuilder()
                    .userName(request.username() != null ? request.username() : userDbExtraEntity.getUserName())
                    .userPhoneNumber(request.phoneNumber() != null ? request.phoneNumber() : userDbExtraEntity.getUserPhoneNumber())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepositoryPort.saveUserDetail(updatedEntity);
        }

        // UserAuthDetailEntity 업데이트
        if (request.department() != null || request.studentId() != null || request.email() != null) {
            UserIdentityEntity userAuthEntity = userRepositoryPort.findUserAuthById(userId)
                    .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 인증 정보를 찾을 수 없습니다."));

            UserIdentityEntity updatedAuthEntity = UserIdentityEntity.builder()
                    .userId(userAuthEntity.getUserId())
                    .department(request.department() != null ? request.department() : userAuthEntity.getDepartment())
                    .studentId(request.studentId() != null ? request.studentId() : userAuthEntity.getStudentId())
                    .authType(userAuthEntity.getAuthType())
                    .userEmail(request.email() != null ? request.email() : userAuthEntity.getUserEmail())
                    .build();

            userRepositoryPort.saveUserIdentity(updatedAuthEntity);
        }

        return userId;
    }


    public UserDbExtraEntity adminGetUserDetailDB(String userId) {
        try {
            UserDbExtraEntity userDetail = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

            return userDetail;
        } catch (AccBaseException e) {
            if (e instanceof AccBaseException) {
                throw e;
            }

            throw new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, e.getMessage());
        }
    }


    /**
     * 관리자 사용자 목록 조회
     * System Admin 권한으로 Keystone 사용자 목록 조회 및 ACC DB 정보 병합
     * 필터링(미가입/삭제 제외) 후에도 요청한 개수를 채우기 위해 반복 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<User> adminListUsers(PageRequest page, String adminToken) {
        List<User> validUsers = new ArrayList<>();
        String currentMarker = page.getMarker();
        String lastNextMarker = null;

        // 요청한 개수를 채울 때까지 반복 조회
        while (validUsers.size() < page.getLimit()) {
            UserListResponse keystoneResponse = keystoneAPIExternalPort.listUsers(
                    adminToken, currentMarker, page.getLimit()
            );

            // Keystone 사용자들을 필터링하여 User 도메인 모델로 변환
            List<User> filtered = filterAndConvertToUsers(keystoneResponse.getUserKeystoneDtos());
            validUsers.addAll(filtered);

            lastNextMarker = keystoneResponse.getNextMarker();

            // Keystone에 더 이상 데이터가 없으면 중단
            if (lastNextMarker == null) {
                break;
            }

            // 요청한 개수를 채웠으면 중단
            if (validUsers.size() >= page.getLimit()) {
                validUsers = validUsers.subList(0, page.getLimit());
                break;
            }

            currentMarker = lastNextMarker;
        }

        return buildUserPageResponse(validUsers, page.getMarker(), lastNextMarker);
    }

    /**
     * Keystone 사용자 목록을 필터링하고 User 도메인 모델로 변환
     * 미가입 사용자 및 삭제된 사용자 제외
     */
    private List<User> filterAndConvertToUsers(List<UserKeystoneDto> userKeystoneDtos) {
        List<String> userIds = userKeystoneDtos.stream()
                .map(UserKeystoneDto::id)
                .toList();

        // ACC DB에서 사용자 정보 bulk 조회 (JOIN 쿼리 1번)
        // 이미 삭제되지 않은 사용자만 필터링되어 반환됨
        List<UserDBDto> userDBDtos = userRepositoryPort.findUserDBsByUserIds(userIds);

        // UserDBDto를 Map으로 변환 (빠른 조회를 위해)
        Map<String, UserDBDto> userDBMap = userDBDtos.stream()
                .collect(Collectors.toMap(UserDBDto::getUserId, dto -> dto));

        // Keystone 데이터와 DB 데이터를 결합하여 User 생성
        return userKeystoneDtos.stream()
                .map(keystoneUser -> {
                    String userId = keystoneUser.id();
                    UserDBDto userDBDto = userDBMap.get(userId);

                    // DB에 없거나 삭제된 사용자는 제외
                    if (userDBDto == null) {
                        return null;
                    }

                    // User 도메인 모델 생성
                    return User.from(
                            keystoneUser,
                            userDBDto.userDbExtra(),
                            userDBDto.userIdentity()
                    );
                })
                .filter(user -> user != null)
                .toList();
    }

    /**
     * 페이지 응답 객체 생성 (User 도메인 모델용)
     */
    private PageResponse<User> buildUserPageResponse(
            List<User> users,
            String requestMarker,
            String nextMarker) {

        return PageResponse.<User>builder()
                .contents(users)
                .first(requestMarker == null || requestMarker.isEmpty())
                .last(nextMarker == null)
                .size(users.size())
                .nextMarker(nextMarker)
                .prevMarker(requestMarker)
                .build();
    }

    /**
     * 관리자 사용자 삭제 처리 (가상 삭제)
     * ACC DB에서 isDeleted 플래그를 true로 설정
     */
    @Transactional
    public void adminDeleteUser(String userId, String adminToken) {
        // ACC DB에서 UserDetailEntity의 isDeleted를 true로 설정
        UserDbExtraEntity userDbExtraEntity = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        UserDbExtraEntity deletedEntity = userDbExtraEntity.toBuilder()
                .isDeleted(true)
                .updatedAt(LocalDateTime.now())
                .build();

        userRepositoryPort.saveUserDetail(deletedEntity);
    }

    /**
     * 관리자 사용자 목록 조회 (Role Assignments API 사용)
     * Role Assignments API를 통해 권한이 할당된 모든 사용자 조회
     * 기존 listUsers API 대비 효율적으로 권한 정보를 함께 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<User> adminListUsersViaRoleAssignments(PageRequest page, String adminToken) {
        List<User> validUsers = new ArrayList<>();
        String currentMarker = page.getMarker();
        String lastNextMarker = null;

        // 요청한 개수를 채울 때까지 반복 조회
        while (validUsers.size() < page.getLimit()) {
            // 1. Role Assignments API로 모든 권한이 할당된 사용자 조회
            Map<String, String> filters = KeystoneAPIUtils.createKeystoneRoleAssignmentsFilters(
                    currentMarker, page.getLimit()
            );

            RoleAssignmentListResponse roleAssignmentResponse = keystoneAPIExternalPort
                    .listRoleAssignments(adminToken, filters);

            // 2. role assignments에서 KeystoneUser 목록 추출 (중복 제거)
            List<UserKeystoneDto> userKeystoneDtos = convertRoleAssignmentsToKeystoneUsers(roleAssignmentResponse);

            // 3. ACC DB에서 해당 사용자들의 정보를 bulk 조회하여 User 도메인 모델로 변환
            List<User> filtered = filterAndConvertToUsers(userKeystoneDtos);
            validUsers.addAll(filtered);

            lastNextMarker = roleAssignmentResponse.getNextMarker();

            // Keystone에 더 이상 데이터가 없으면 중단
            if (lastNextMarker == null) {
                break;
            }

            // 요청한 개수를 채웠으면 중단
            if (validUsers.size() >= page.getLimit()) {
                validUsers = validUsers.subList(0, page.getLimit());
                break;
            }

            currentMarker = lastNextMarker;
        }

        return buildUserPageResponse(validUsers, page.getMarker(), lastNextMarker);
    }

    /**
     * Role Assignments를 KeystoneUser 목록으로 변환
     * user가 있는 assignment만 추출하고 userId 중복 제거
     */
    private List<UserKeystoneDto> convertRoleAssignmentsToKeystoneUsers(RoleAssignmentListResponse response) {
        return response.getRoleAssignments().stream()
                .filter(assignment -> assignment.getUser() != null) // NPE 처리
                .map(assignment -> UserKeystoneDto.builder()
                        .id(assignment.getUser().getId())
                        .name(assignment.getUser().getName())
                        .domainId(assignment.getUser().getDomain() != null ?
                                assignment.getUser().getDomain().getId() : null)
                        .enabled(true) // role이 할당되어 있다는 것은 활성 사용자
                        .build())
                .collect(Collectors.toMap(
                        UserKeystoneDto::id,
                        user -> user,
                        (existing, replacement) -> existing // 중복 시 첫 번째 유지
                ))
                .values()
                .stream()
                .toList();
    }

    // 요청자의 UserId를 통해서 관리자인지 확인할 수 있는 메서드
    @Transactional
    public void isAdminUser(String requesterId) {
        UserDbExtraEntity requesterDetail = userRepositoryPort.findUserDetailById(requesterId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "관리자 정보를 찾을 수 없습니다."));

        // 삭제된 사용자는 권한 없음
        if (requesterDetail.getIsDeleted()) {
            throw new AuthServiceException(AuthErrorCode.FORBIDDEN_ACCESS, "삭제된 사용자입니다.");
        }

        if (!requesterDetail.getIsAdmin()) {
            throw new AuthServiceException(AuthErrorCode.FORBIDDEN_ACCESS, "관리자 권한이 필요한 기능입니다.");
        }
    }

    /**
     * 재사용 가능한 User 조회 메서드
     * Keystone + DB 정보를 조합하여 완전한 User 도메인 모델 반환
     *
     * @param userId 조회할 사용자 ID
     * @param adminToken Keystone 조회에 사용할 관리자 토큰
     * @return 완전한 User 도메인 모델 (모든 필드가 채워짐)
     * @throws AuthServiceException Keystone에는 존재하지만 DB에 없는 경우 정합성 에러 발생
     */
    @Transactional(readOnly = true)
    public User getUserById(String userId, String adminToken) {
        // 1. Keystone에서 사용자 정보 조회
        UserKeystoneDto userKeystoneDto = keystoneAPIExternalPort.getUserDetail(userId, adminToken);

        // 2. DB에서 사용자 정보 조회 (조인 쿼리로 한 번에 가져옴)
        // Keystone에 존재하는데 DB에 없으면 정합성 불일치 에러
        UserDBDto userDBDto = userRepositoryPort.findUserDBByUserId(userId)
                .orElseThrow(() -> new AuthServiceException(
                        AuthErrorCode.USER_DATA_INCONSISTENCY
                ));

        // 3. User 도메인 모델 생성 및 반환 (모든 필드가 완전히 채워짐)
        return User.from(
                userKeystoneDto,
                userDBDto.userDbExtra(),
                userDBDto.userIdentity()
        );
    }

}
