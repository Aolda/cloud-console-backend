package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.RoleDocs;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;
import com.acc.local.service.ports.RoleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoleController implements RoleDocs {

    private final RoleServicePort roleServicePort;


    @Override
    @PostMapping("")
    @Deprecated
    public ResponseEntity<CreateRoleResponse> createRole(
            @RequestBody @Validated CreateRoleRequest request,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        CreateRoleResponse response = roleServicePort.adminCreateRole(request, sessionId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("")
    @Deprecated
    public ResponseEntity<PageResponse<ListRolesResponse>> listRoles(
            PageRequest page,
            String name,
            Authentication authentication
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        PageResponse<ListRolesResponse> response = roleServicePort.adminListRoles(page, name, sessionId);

        return ResponseEntity.ok(response);
    }
}

