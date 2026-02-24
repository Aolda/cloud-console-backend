package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.RouterDocs;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.service.ports.RouterServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RouterController implements RouterDocs {

    private final RouterServicePort routerServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewRoutersResponse>> viewRouters(Authentication authentication, PageRequest page, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(routerServicePort.listRouters(page, principal.getSessionId(), projectId));
    }

    @Override
    public ResponseEntity<Object> createRouter(Authentication authentication, CreateRouterRequest request, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String id = routerServicePort.createRouter(request, principal.getSessionId(), projectId);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteNetwork(Authentication authentication, String routerId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();

        routerServicePort.deleteRouter(routerId, principal.getSessionId(), projectId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> connectRouterToSubnet(Authentication authentication, String routerId, String subnetId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        routerServicePort.connectRouterToSubnet(routerId, subnetId, principal.getSessionId(), projectId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> disconnectRouterFromSubnet(Authentication authentication, String routerId, String subnetId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        routerServicePort.disconnectRouterFromSubnet(routerId, subnetId, principal.getSessionId(), projectId);
        return ResponseEntity.ok().build();
    }
}
