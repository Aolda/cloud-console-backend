package com.acc.local.service.ports;

import com.acc.dto.port.*;
import java.util.List;

public interface PortPort {
    PortResponse createPort(PortRequest request, String token);
    List<PortResponse> listPorts(String token);
    PortResponse updatePort(String id, PortUpdateRequest request, String token);
    void deletePort(String id, String token);
    List<PortResponse> searchPorts(String keyword, String token);
}