package com.acc.local.external.ports;

import com.acc.local.dto.instance.InstanceActionRequest;

public interface NovaServerActionExternalPort {

    void callControlInstance(String token, String projectId, String instanceId, InstanceActionRequest request);
}
