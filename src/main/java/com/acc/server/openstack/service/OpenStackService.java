package com.acc.server.openstack.service;

import com.acc.server.openstack.domain.port.KeystonePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenStackService {
    private final KeystonePort keystonePort;
    // …

    public String getToken(String user, String pwd, String project) {
        return keystonePort.issueToken(user, pwd, project);
    }
}
