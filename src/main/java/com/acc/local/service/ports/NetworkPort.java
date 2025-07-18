package com.acc.local.service.ports;

import com.acc.dto.network.*;
import java.util.List;

public interface NetworkPort {
    NetworkResponse createNetwork(NetworkRequest request, String token);
    List<NetworkResponse> listNetworks(String token);
    NetworkResponse updateNetwork(String id, NetworkUpdateRequest request, String token);
    void deleteNetwork(String id, String token);
}
