package com.acc.local.controller;

import com.acc.global.annotation.LogDomain;
import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.NetworkDocs;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.ports.NetworkServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@LogDomain("network")
public class NetworkController implements NetworkDocs {

    private final NetworkServicePort networkServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewNetworksResponse>> viewNetworks(
            Authentication authentication,
            PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        PageResponse<ViewNetworksResponse> response =
                networkServicePort.listNetworks(page, jwtInfo.getUserId(), jwtInfo.getProjectId());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> createNetwork(
            Authentication authentication,
            CreateNetworkRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        networkServicePort.createNetwork(request, jwtInfo.getUserId(), jwtInfo.getProjectId());
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteNetwork(
            Authentication authentication,
            String networkId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        networkServicePort.deleteNetwork(networkId, jwtInfo.getUserId(), jwtInfo.getProjectId());
        return ResponseEntity.noContent().build();
    }
}
