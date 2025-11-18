package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.model.auth.User;
import com.acc.local.domain.model.auth.UserAuthDetail;
import com.acc.local.domain.model.auth.UserDetail;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        // 1. Keystone 사용자 생성 요청 생성 (email을 name에 매핑!)
        User newUser = User.builder()
                .name(request.email()) // email을 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();

        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUserRequest(newUser);

        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.createUser(adminToken, userRequest);

        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
        }

        // 2. Keystone 응답에서 userId 추출
        User createdUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);
        String userId = createdUser.getId();

        // 3. UserDetail 도메인 모델 생성 및 저장
        UserDetail userDetail = UserDetail.createForAdmin(userId, request);
        UserDetailEntity userDetailEntity = userRepositoryPort.saveUserDetail(userDetail.toEntity());

        // 4. UserAuthDetail 도메인 모델 생성 및 저장
        UserAuthDetail userAuthDetail = UserAuthDetail.createForAdmin(userId, request);
        userRepositoryPort.saveUserAuth(userAuthDetail.toEntity(userDetailEntity));

        return userId;
    }

    /**
     * 관리자 사용자 수정 처리
     * System Admin 권한으로 Keystone 사용자 수정 및 ACC DB 업데이트
     */
    @Transactional
    public String adminUpdateUser(AdminUpdateUserRequest request, String adminToken , String userId) {


        // 1. Keystone 사용자 업데이트
        User updateUser = User.builder()
                .name(request.email() != null ? request.email() : null) // email을 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();

        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUpdateUserRequest(updateUser);
        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.updateUser(userId, adminToken, userRequest);

        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 업데이트 응답이 null입니다.");
        }

        // 2. ACC DB 업데이트
        // UserDetailEntity 업데이트
        if (request.username() != null || request.phoneNumber() != null) {
            UserDetailEntity userDetailEntity = userRepositoryPort.findUserDetailById(userId)
                    .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

            UserDetailEntity updatedEntity = userDetailEntity.toBuilder()
                    .userName(request.username() != null ? request.username() : userDetailEntity.getUserName())
                    .userPhoneNumber(request.phoneNumber() != null ? request.phoneNumber() : userDetailEntity.getUserPhoneNumber())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepositoryPort.saveUserDetail(updatedEntity);
        }

        // UserAuthDetailEntity 업데이트
        if (request.department() != null || request.studentId() != null || request.email() != null) {
            UserAuthDetailEntity userAuthEntity = userRepositoryPort.findUserAuthById(userId)
                    .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 인증 정보를 찾을 수 없습니다."));

            UserAuthDetailEntity updatedAuthEntity = UserAuthDetailEntity.builder()
                    .userId(userAuthEntity.getUserId())
                    .department(request.department() != null ? request.department() : userAuthEntity.getDepartment())
                    .studentId(request.studentId() != null ? request.studentId() : userAuthEntity.getStudentId())
                    .authType(userAuthEntity.getAuthType())
                    .userEmail(request.email() != null ? request.email() : userAuthEntity.getUserEmail())
                    .build();

            userRepositoryPort.saveUserAuth(updatedAuthEntity);
        }

        return userId;
    }

    /**
     * 관리자 사용자 상세 조회
     * System Admin 권한으로 Keystone 사용자 조회 및 ACC DB 정보 병합
     */
    @Transactional(readOnly = true)
    public AdminGetUserResponse adminGetUser(String userId, String adminToken) {
        // 1. Keystone에서 사용자 정보 조회
        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.getUserDetail(userId, adminToken);
        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        User keystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);

        // 2. ACC DB에서 추가 정보 조회
        UserDetailEntity userDetail = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        UserAuthDetailEntity userAuth = userRepositoryPort.findUserAuthById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 인증 정보를 찾을 수 없습니다."));

        // 4. 병합하여 반환
        return AdminGetUserResponse.builder()
                .userId(keystoneUser.getId())
                .username(userDetail.getUserName())
                .email(userAuth.getUserEmail())
                .department(userAuth.getDepartment())
                .studentId(userAuth.getStudentId())
                .phoneNumber(userDetail.getUserPhoneNumber())
                .isEnabled(keystoneUser.isEnabled())
                .isAdmin(userDetail.getIsAdmin())
                .isDeleted(userDetail.getIsDeleted())
                .build();
    }

    /**
     * 관리자 사용자 목록 조회
     * System Admin 권한으로 Keystone 사용자 목록 조회 및 ACC DB 정보 병합
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminListUsersResponse> adminListUsers(
            PageRequest page, String adminToken) {

        // 1. Keystone에서 사용자 목록 조회 (UserListResponse 모델 반환)
        UserListResponse userListResponse = keystoneAPIExternalPort.listUsers(
                adminToken,
                page.getMarker(),
                page.getLimit()
        );

        // 2. 모든 userId 추출
        List<String> userIds = userListResponse.getUsers().stream()
                .map(User::getId)
                .toList();

        // 3. ACC DB에서 모든 사용자 정보 bulk 조회
        List<UserDetailEntity> userDetails = userRepositoryPort.findUserDetailsByIds(userIds);
        List<UserAuthDetailEntity> userAuths = userRepositoryPort.findUserAuthsByIds(userIds);

        // 4. userId를 키로 하는 Map 생성
        Map<String, UserDetailEntity> userDetailMap = userDetails.stream()
                .collect(Collectors.toMap(UserDetailEntity::getUserId, entity -> entity));
        Map<String, UserAuthDetailEntity> userAuthMap = userAuths.stream()
                .collect(Collectors.toMap(UserAuthDetailEntity::getUserId, entity -> entity));

        // 5. User 모델 리스트를 DTO 리스트로 변환 및 ACC DB 정보 병합
        List<AdminListUsersResponse> userList = new ArrayList<>();

        for (User keystoneUser : userListResponse.getUsers()) {
            String userId = keystoneUser.getId();
            String keystoneName = keystoneUser.getName();
            boolean enabled = keystoneUser.isEnabled();
           // String defaultProjectId = keystoneUser.getDefaultProjectId();

            // Map에서 정보 조회
            UserDetailEntity userDetail = userDetailMap.get(userId);
            UserAuthDetailEntity userAuth = userAuthMap.get(userId);

            // 삭제된 사용자는 목록에서 제외
            if (userDetail != null && userDetail.getIsDeleted()) {
                continue;
            }

            // 기본 프로젝트 이름 조회 (defaultProjectId가 있는 경우)
            String defaultProjectName = null;
//            if (defaultProjectId != null && !defaultProjectId.isEmpty()) {
//                try {
//                    ResponseEntity<JsonNode> projectResponse = keystoneAPIExternalPort.getProjectDetail(defaultProjectId, adminToken);
//                    if (projectResponse != null && projectResponse.getBody() != null) {
//                        defaultProjectName = projectResponse.getBody().path("project").path("name").asText(null);
//                    }
//                } catch (Exception e) {
//                    log.warn("Failed to fetch project name for project ID: {}", defaultProjectId);
//                }
//            }

            // Response 객체 생성
            AdminListUsersResponse userResponse =
                    AdminListUsersResponse.builder()
                            .userId(userId)
                            .username(userDetail != null ? userDetail.getUserName() : keystoneName)
                            .isAdmin(userDetail != null ? userDetail.getIsAdmin() : false)
                            .email(userAuth != null ? userAuth.getUserEmail() : null)
                            .phoneNumber(userDetail != null ? userDetail.getUserPhoneNumber() : null)
                            .department(userAuth != null ? userAuth.getDepartment() : null)
                            .enabled(enabled)
                            .defaultProjectName(defaultProjectName)
                            .build();

            userList.add(userResponse);
        }

        // 3. 페이지 응답 구성
        return PageResponse.<AdminListUsersResponse>builder()
                .contents(userList)
                .first(page.getMarker() == null || page.getMarker().isEmpty())
                .last(userListResponse.getNextMarker() == null)
                .size(userList.size())
                .nextMarker(userListResponse.getNextMarker())
                .prevMarker(userListResponse.getPrevMarker())
                .build();
    }

    /**
     * 관리자 사용자 삭제 처리 (가상 삭제)
     * ACC DB에서 isDeleted 플래그를 true로 설정
     */
    @Transactional
    public void adminDeleteUser(String userId, String adminToken) {
        // ACC DB에서 UserDetailEntity의 isDeleted를 true로 설정
        UserDetailEntity userDetailEntity = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        UserDetailEntity deletedEntity = userDetailEntity.toBuilder()
                .isDeleted(true)
                .updatedAt(LocalDateTime.now())
                .build();

        userRepositoryPort.saveUserDetail(deletedEntity);
    }

    // 요청자의 UserId를 통해서 관리자인지 확인할 수 있는 메서드
    @Transactional
    public void isAdminUser(String requesterId) {
        UserDetailEntity requesterDetail = userRepositoryPort.findUserDetailById(requesterId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "관리자 정보를 찾을 수 없습니다."));

        // 삭제된 사용자는 권한 없음
        if (requesterDetail.getIsDeleted()) {
            throw new AuthServiceException(AuthErrorCode.FORBIDDEN_ACCESS, "삭제된 사용자입니다.");
        }

        if (!requesterDetail.getIsAdmin()) {
            throw new AuthServiceException(AuthErrorCode.FORBIDDEN_ACCESS, "관리자 권한이 필요한 기능입니다.");
        }
    }

}
