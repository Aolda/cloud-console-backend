package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.InterfaceDocs;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.ports.InterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InterfaceController implements InterfaceDocs {

    private final InterfaceServicePort interfaceServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewInterfacesResponse>> viewInterfaces(String token, PageRequest page, String instanceId, String networkId) {
        return ResponseEntity.ok(
                interfaceServicePort.listInterfaces(page, token, instanceId, networkId)
        );
    }

    @Override
    public ResponseEntity<Object> createInterface(String token, CreateInterfaceRequest request) {
        interfaceServicePort.createInterface(token, request);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteInterface(String token, String interfaceId) {
        interfaceServicePort.deleteInterface(token, interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> allocateExternalIp(String token, String interfaceId) {
        interfaceServicePort.allocateExternalIp(token, interfaceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> releaseExternalIp(String token, String interfaceId) {
        interfaceServicePort.releaseExternalIp(token, interfaceId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> createPortForwarding(String token, String interfaceId) {
        interfaceServicePort.createSSHForwarding(token, interfaceId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> deletePortForwarding(String token, String interfaceId) {
        interfaceServicePort.deleteSSHForwarding(token, interfaceId);
        return ResponseEntity.noContent().build();
    }
}
