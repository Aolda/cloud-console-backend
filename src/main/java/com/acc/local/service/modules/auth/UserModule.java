package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.model.auth.KeystoneUser;
import com.acc.local.domain.model.auth.RoleAssignmentListResponse;
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
import java.util.HashMap;
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
        KeystoneUser newKeystoneUser = KeystoneUser.builder()
                .name(request.email()) // email을 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();

        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUserRequest(newKeystoneUser);

        ResponseEntity<JsonNode> response = keystoneAPIExternalPort.createUser(adminToken, userRequest);

        if (response == null) {
            throw new AuthServiceException(AuthErrorCode.KEYSTONE_USER_CREATION_FAILED, "사용자 생성 응답이 null입니다.");
        }

        // 2. Keystone 응답에서 userId 추출
        KeystoneUser createdKeystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);
        String userId = createdKeystoneUser.getId();

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
        KeystoneUser updateKeystoneUser = KeystoneUser.builder()
                .name(request.email() != null ? request.email() : null) // email을 name(아이디)로 사용
                .password(request.password())
                .enabled(request.isEnabled())
                .build();

        Map<String, Object> userRequest = KeystoneAPIUtils.createKeystoneUpdateUserRequest(updateKeystoneUser);
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
        KeystoneUser keystoneUser = KeystoneAPIUtils.parseKeystoneUserResponse(response);

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

    public UserDetailEntity adminGetUserDetailDB(String userId) {
        UserDetailEntity userDetail = userRepositoryPort.findUserDetailById(userId)
            .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        return userDetail;
    }

    /**
     * 관리자 사용자 목록 조회
     * System Admin 권한으로 Keystone 사용자 목록 조회 및 ACC DB 정보 병합
     * 필터링(미가입/삭제 제외) 후에도 요청한 개수를 채우기 위해 반복 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminListUsersResponse> adminListUsers(PageRequest page, String adminToken) {
        List<AdminListUsersResponse> validUsers = new ArrayList<>();
        String currentMarker = page.getMarker();
        String lastNextMarker = null;

        // 요청한 개수를 채울 때까지 반복 조회
        while (validUsers.size() < page.getLimit()) {
            UserListResponse keystoneResponse = keystoneAPIExternalPort.listUsers(
                    adminToken, currentMarker, page.getLimit()
            );

            // Keystone 사용자들을 필터링하여 유효한 사용자만 추가
            List<AdminListUsersResponse> filtered = filterAndConvertUsers(keystoneResponse.getKeystoneUsers());
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

        return buildPageResponse(validUsers, page.getMarker(), lastNextMarker);
    }

    /**
     * Keystone 사용자 목록을 필터링하고 DTO로 변환
     * 미가입 사용자 및 삭제된 사용자 제외
     */
    private List<AdminListUsersResponse> filterAndConvertUsers(List<KeystoneUser> keystoneUsers) {
        List<String> userIds = keystoneUsers.stream()
                .map(KeystoneUser::getId)
                .toList();

        // ACC DB에서 사용자 정보 bulk 조회
        Map<String, UserDetailEntity> userDetailMap = userRepositoryPort.findUserDetailsByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserDetailEntity::getUserId, entity -> entity));

        Map<String, UserAuthDetailEntity> userAuthMap = userRepositoryPort.findUserAuthsByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserAuthDetailEntity::getUserId, entity -> entity));

        // 필터링 및 변환
        return keystoneUsers.stream()
                .map(keystoneUser -> convertToAdminListResponse(keystoneUser, userDetailMap, userAuthMap))
                .filter(response -> response != null)
                .toList();
    }

    /**
     * Keystone 사용자를 AdminListUsersResponse로 변환
     * 미가입 또는 삭제된 사용자는 null 반환
     */
    private AdminListUsersResponse convertToAdminListResponse(
            KeystoneUser keystoneUser,
            Map<String, UserDetailEntity> userDetailMap,
            Map<String, UserAuthDetailEntity> userAuthMap) {

        String userId = keystoneUser.getId();
        UserDetailEntity userDetail = userDetailMap.get(userId);

        // 미가입 사용자 또는 삭제된 사용자는 제외
        if (userDetail == null || userDetail.getIsDeleted()) {
            return null;
        }

        UserAuthDetailEntity userAuth = userAuthMap.get(userId);

        return AdminListUsersResponse.builder()
                .userId(userId)
                .username(userDetail.getUserName())
                .isAdmin(userDetail.getIsAdmin())
                .email(userAuth != null ? userAuth.getUserEmail() : null)
                .phoneNumber(userDetail.getUserPhoneNumber())
                .department(userAuth != null ? userAuth.getDepartment() : null)
                .enabled(keystoneUser.isEnabled())
                .defaultProjectName(null)
                .build();
    }

    /**
     * 페이지 응답 객체 생성
     */
    private PageResponse<AdminListUsersResponse> buildPageResponse(
            List<AdminListUsersResponse> users,
            String requestMarker,
            String nextMarker) {

        return PageResponse.<AdminListUsersResponse>builder()
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
        UserDetailEntity userDetailEntity = userRepositoryPort.findUserDetailById(userId)
                .orElseThrow(() -> new AuthServiceException(AuthErrorCode.USER_NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        UserDetailEntity deletedEntity = userDetailEntity.toBuilder()
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
    public PageResponse<AdminListUsersResponse> adminListUsersViaRoleAssignments(PageRequest page, String adminToken) {
        List<AdminListUsersResponse> validUsers = new ArrayList<>();
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
            List<KeystoneUser> keystoneUsers = convertRoleAssignmentsToKeystoneUsers(roleAssignmentResponse);

            // 3. ACC DB에서 해당 사용자들의 정보를 bulk 조회하여 필터링
            List<AdminListUsersResponse> filtered = filterAndConvertUsers(keystoneUsers);
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

        return buildPageResponse(validUsers, page.getMarker(), lastNextMarker);
    }

    /**
     * Role Assignments를 KeystoneUser 목록으로 변환
     * user가 있는 assignment만 추출하고 userId 중복 제거
     */
    private List<KeystoneUser> convertRoleAssignmentsToKeystoneUsers(RoleAssignmentListResponse response) {
        return response.getRoleAssignments().stream()
                .filter(assignment -> assignment.getUser() != null) // NPE 처리
                .map(assignment -> KeystoneUser.builder()
                        .id(assignment.getUser().getId())
                        .name(assignment.getUser().getName())
                        .domainId(assignment.getUser().getDomain() != null ?
                                assignment.getUser().getDomain().getId() : null)
                        .enabled(true) // role이 할당되어 있다는 것은 활성 사용자
                        .build())
                .collect(Collectors.toMap(
                        KeystoneUser::getId,
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
