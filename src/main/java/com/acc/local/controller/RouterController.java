package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.RouterDocs;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.service.ports.RouterServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouterController implements RouterDocs {

    private final RouterServicePort routerServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewRoutersResponse>> viewRouters(Authentication authentication, PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(routerServicePort.listRouters(page, jwtInfo.getUserId(), jwtInfo.getProjectId()));
    }

    @Override
    public ResponseEntity<Object> createRouter(Authentication authentication, CreateRouterRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        routerServicePort.createRouter(request, jwtInfo.getUserId(), jwtInfo.getProjectId());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> deleteNetwork(Authentication authentication, String routerId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();

        routerServicePort.deleteRouter(routerId, jwtInfo.getUserId(), jwtInfo.getProjectId());
        return ResponseEntity.noContent().build();
    }
}
