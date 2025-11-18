package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.domain.model.auth.Role;
import com.acc.local.domain.model.auth.RoleListResponse;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;
import com.acc.local.external.ports.KeystoneAPIExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleModule {

    private final KeystoneAPIExternalPort keystoneAPIExternalPort;

    /**
     * 관리자 역할 생성 처리
     * System Admin 권한으로 Keystone Role 생성
     */
    @Transactional
    public CreateRoleResponse adminCreateRole(CreateRoleRequest request, String adminToken) {
        // 1. Keystone Role 생성 요청 생성
        Map<String, Object> roleRequest = new HashMap<>();
        Map<String, Object> roleData = new HashMap<>();

        roleData.put("name", request.name());
        if (request.description() != null) {
            roleData.put("description", request.description());
        }
        if (request.domainId() != null) {
            roleData.put("domain_id", request.domainId());
        }

        roleRequest.put("role", roleData);

        // 2. Keystone에서 Role 모델 반환
        Role role = keystoneAPIExternalPort.createRole(adminToken, roleRequest);

        // 3. Role 모델을 DTO로 변환
        return CreateRoleResponse.from(role.getId(), role.getName(), role.getDescription(), role.getDomainId());
    }

    /**
     * 관리자 역할 목록 조회
     * System Admin 권한으로 Keystone Role 목록 조회
     */
    @Transactional
    public PageResponse<ListRolesResponse> adminListRoles(PageRequest page, String name, String adminToken) {

        // 1. Keystone에서 역할 목록 조회 (RoleListResponse 모델 반환)
        RoleListResponse roleListResponse = keystoneAPIExternalPort.listRoles(
                adminToken,
                page.getMarker(),
                page.getLimit(),
                name
        );

        // 2. Role 모델 리스트를 DTO 리스트로 변환
        List<ListRolesResponse> roleList = roleListResponse.getRoles().stream()
                .map(role -> ListRolesResponse.from(
                        role.getId(),
                        role.getName(),
                        role.getDescription(),
                        role.getDomainId()
                ))
                .toList();

        // 3. 페이지 응답 구성
        return PageResponse.<ListRolesResponse>builder()
                .contents(roleList)
                .first(page.getMarker() == null || page.getMarker().isEmpty())
                .last(roleListResponse.getNextMarker() == null)
                .size(roleList.size())
                .nextMarker(roleListResponse.getNextMarker())
                .prevMarker(roleListResponse.getPrevMarker())
                .build();
    }
}
