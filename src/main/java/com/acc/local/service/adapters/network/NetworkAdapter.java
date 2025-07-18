package com.acc.local.service.adapters.network;

import com.acc.dto.network.*;
import com.acc.local.service.modules.network.NetworkModule;
import com.acc.local.service.ports.NetworkPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class NetworkAdapter implements NetworkPort {

    private final NetworkModule networkModule;

    @Override
    public NetworkResponse createNetwork(NetworkRequest request, String token) {
        return networkModule.createNetwork(request, token);
    }

    @Override
    public List<NetworkResponse> listNetworks(String token) {
        return networkModule.listNetworks(token);
    }

    @Override
    public NetworkResponse updateNetwork(String id, NetworkUpdateRequest request, String token) {
        return networkModule.updateNetwork(id, request, token);
    }

    @Override
    public void deleteNetwork(String id, String token) {
        networkModule.deleteNetwork(id, token);
    }
}