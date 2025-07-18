package com.acc.local.service.adapters.port;

import com.acc.dto.port.*;
import com.acc.local.service.modules.port.PortModule;
import com.acc.local.service.ports.PortPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortAdapter implements PortPort {

    private final PortModule portModule;
    private final NovaModule novaModule;
    private final RouterModule routerModule;

    @Override
    public PortResponse createPort(PortRequest request, String token) {
        return portModule.createPort(request, token);
    }

    @Override
    public List<PortResponse> listPorts(String token) {
        List<PortResponse> ports = portModule.listPorts(token);
        for (PortResponse port : ports) {
            String owner = port.getDeviceOwner();
            if (owner != null && owner.startsWith("compute")) {
                port.setDeviceName(novaModule.getInstanceName(port.getDeviceId(), token));
            } else if (owner != null && owner.contains("router")) {
                port.setDeviceName(routerModule.getRouterName(port.getDeviceId(), token));
            } else {
                port.setDeviceName("(unknown)");
            }
        }
        return ports;
    }

    @Override
    public PortResponse updatePort(String id, PortUpdateRequest request, String token) {
        return portModule.updatePort(id, request, token);
    }

    @Override
    public void deletePort(String id, String token) {
        portModule.deletePort(id, token);
    }

    @Override
    public List<PortResponse> searchPorts(String keyword, String token) {
        return portModule.listPorts(token).stream()
                .filter(port -> port.getName() != null && port.getName().contains(keyword))
                .sorted(Comparator.comparing(PortResponse::getName))
                .collect(Collectors.toList());
    }
}