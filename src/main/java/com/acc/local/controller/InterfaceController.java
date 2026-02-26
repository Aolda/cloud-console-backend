package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.InterfaceDocs;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.ports.InterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class InterfaceController implements InterfaceDocs {

    private final InterfaceServicePort interfaceServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewInterfacesResponse>> viewInterfaces(Authentication authentication, PageRequest page, String instanceId, String networkId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(
                interfaceServicePort.listInterfaces(page, principal.getSessionId(), projectId, instanceId, networkId)
        );
    }

    @Override
    public ResponseEntity<Object> createInterface(Authentication authentication, CreateInterfaceRequest request, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String id = interfaceServicePort.createInterface(principal.getSessionId(), projectId, request);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteInterface(Authentication authentication, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        interfaceServicePort.deleteInterface(principal.getSessionId(), projectId, interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> allocateExternalIp(Authentication authentication, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        interfaceServicePort.allocateExternalIp(principal.getSessionId(), projectId, interfaceId);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> releaseExternalIp(Authentication authentication, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        interfaceServicePort.releaseExternalIp(principal.getSessionId(), projectId, interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> createPortForwarding(Authentication authentication, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        interfaceServicePort.createSSHForwarding(principal.getSessionId(), projectId, interfaceId);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deletePortForwarding(Authentication authentication, String interfaceId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        interfaceServicePort.deleteSSHForwarding(principal.getSessionId(), projectId, interfaceId);
        return ResponseEntity.noContent().build();
    }
}
