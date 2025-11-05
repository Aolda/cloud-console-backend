package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.RouterDocs;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.service.ports.RouterServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouterController implements RouterDocs {

    private final RouterServicePort routerServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewRoutersResponse>> viewRouters(String token, PageRequest page) {
        return ResponseEntity.ok(routerServicePort.listRouters(page, token));
    }

    @Override
    public ResponseEntity<Object> createRouter(String token, CreateRouterRequest request) {
        routerServicePort.createRouter(request, token);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> deleteNetwork(String token, String routerId) {

        routerServicePort.deleteRouter(routerId, token);
        return ResponseEntity.noContent().build();
    }
}
