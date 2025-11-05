package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.NetworkDocs;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.ports.NetworkServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NetworkController implements NetworkDocs {

    private final NetworkServicePort networkServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewNetworksResponse>> viewNetworks(
            String token,
            PageRequest page) {

        PageResponse<ViewNetworksResponse> response =
                networkServicePort.listNetworks(page, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> createNetwork(
            String token,
            CreateNetworkRequest request) {

        networkServicePort.createNetwork(request, token);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteNetwork(
            String token,
            String networkId) {
        networkServicePort.deleteNetwork(networkId, token);
        return ResponseEntity.noContent().build();
    }
}
