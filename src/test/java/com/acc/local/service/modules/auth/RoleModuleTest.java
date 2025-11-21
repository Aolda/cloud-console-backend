package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.domain.model.auth.Role;
import com.acc.local.domain.model.auth.RoleListResponse;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;
import com.acc.local.external.ports.KeystoneAPIExternalPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleModuleTest {

    @Mock
    private KeystoneAPIExternalPort keystoneAPIExternalPort;

    @InjectMocks
    private RoleModule roleModule;

    // --------------------------------------------------------------------
    // 역할 생성 테스트
    // --------------------------------------------------------------------
    @Test
    @DisplayName("관리자는 System Admin 토큰으로 Keystone Role을 생성할 수 있다.")
    void givenCreateRoleRequest_whenAdminCreateRole_thenReturnRoleResponse() {

        // given
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("admin-role")
                .description("관리자 역할")
                .domainId("default")
                .build();

        String adminToken = "system-admin-token";

        Role role = Role.builder()
                .id("role-1234")
                .name("admin-role")
                .description("관리자 역할")
                .domainId("default")
                .build();

        when(keystoneAPIExternalPort.createRole(eq(adminToken), any()))
                .thenReturn(role);

        // when
        CreateRoleResponse response = roleModule.adminCreateRole(request, adminToken);

        // then
        assertNotNull(response);
        assertEquals("role-1234", response.roleId());
        assertEquals("admin-role", response.name());
        assertEquals("관리자 역할", response.description());
        assertEquals("default", response.domainId());

        verify(keystoneAPIExternalPort).createRole(eq(adminToken), any());
    }

    // --------------------------------------------------------------------
    // 역할 목록 조회 테스트
    // --------------------------------------------------------------------
    @Test
    @DisplayName("관리자는 Keystone Role 목록을 조회하고 DTO 리스트로 변환할 수 있다.")
    void givenPageRequest_whenAdminListRoles_thenReturnPagedRoles() {

        // given
        PageRequest page = new PageRequest();
        page.setMarker(null);
        page.setLimit(10);

        String adminToken = "system-admin-token";
        String searchName = "admin";

        Role role1 = Role.builder()
                .id("r1")
                .name("admin-role")
                .description("관리자용 역할")
                .domainId("default")
                .build();

        Role role2 = Role.builder()
                .id("r2")
                .name("user-role")
                .description("사용자 역할")
                .domainId("default")
                .build();

        RoleListResponse roleListResponse = RoleListResponse.builder()
                .roles(List.of(role1, role2))
                .nextMarker("r2")
                .prevMarker(null)
                .build();

        when(keystoneAPIExternalPort.listRoles(eq(adminToken), any(), anyInt(), eq(searchName)))
                .thenReturn(roleListResponse);

        // when
        PageResponse<ListRolesResponse> response =
                roleModule.adminListRoles(page, searchName, adminToken);

        // then
        assertNotNull(response);
        assertEquals(2, response.getContents().size());
        assertTrue(response.getFirst());
        assertFalse(response.getLast());
        assertEquals("r2", response.getNextMarker());

        ListRolesResponse first = response.getContents().get(0);
        assertEquals("r1", first.roleId());
        assertEquals("admin-role", first.name());
        assertEquals("관리자용 역할", first.description());
        assertEquals("default", first.domainId());

        verify(keystoneAPIExternalPort).listRoles(eq(adminToken), any(), anyInt(), eq(searchName));
    }
}
