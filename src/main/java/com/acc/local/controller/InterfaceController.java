package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.InterfaceDocs;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.ports.InterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InterfaceController implements InterfaceDocs {

    private final InterfaceServicePort interfaceServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewInterfacesResponse>> viewInterfaces(Authentication authentication, PageRequest page, String instanceId, String networkId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(
                interfaceServicePort.listInterfaces(page, jwtInfo.getUserId(), jwtInfo.getProjectId(), instanceId, networkId)
        );
    }

    @Override
    public ResponseEntity<Object> createInterface(Authentication authentication, CreateInterfaceRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.createInterface(jwtInfo.getUserId(), jwtInfo.getProjectId(), request);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteInterface(Authentication authentication, String interfaceId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.deleteInterface(jwtInfo.getUserId(), jwtInfo.getProjectId(), interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> allocateExternalIp(Authentication authentication, String interfaceId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.allocateExternalIp(jwtInfo.getUserId(), jwtInfo.getProjectId(), interfaceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> releaseExternalIp(Authentication authentication, String interfaceId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.releaseExternalIp(jwtInfo.getUserId(), jwtInfo.getProjectId(), interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> createPortForwarding(Authentication authentication, String interfaceId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.createSSHForwarding(jwtInfo.getUserId(), jwtInfo.getProjectId(), interfaceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> deletePortForwarding(Authentication authentication, String interfaceId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        interfaceServicePort.deleteSSHForwarding(jwtInfo.getUserId(), jwtInfo.getProjectId(), interfaceId);
        return ResponseEntity.noContent().build();
    }
}
