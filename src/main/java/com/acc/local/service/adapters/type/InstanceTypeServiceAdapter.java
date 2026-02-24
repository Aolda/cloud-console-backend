package com.acc.local.service.adapters.type;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.type.InstanceTypeErrorCode;
import com.acc.global.exception.type.InstanceTypeException;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.modules.type.InstanceTypeModule;
import com.acc.local.service.modules.type.InstanceTypeUtil;
import com.acc.local.service.ports.InstanceTypeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class InstanceTypeServiceAdapter implements InstanceTypeServicePort {

    private final AuthModule authModule;
    private final SessionModule sessionModule;
    private final InstanceTypeModule instanceTypeModule;
    private final InstanceTypeUtil instanceTypeUtil;

    @Override
    public PageResponse<InstanceTypeResponse> listUserInstanceTypes(String sessionId, String projectId, String architect, PageRequest page) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        return instanceTypeModule.listInstanceTypesByArchitect(
                keystoneToken,
                architect,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public PageResponse<InstanceTypeResponse> listAdminInstanceTypes(String sessionId, String architect, PageRequest page) {
        String userId = sessionModule.getKeystoneUserId(sessionId);
        String keystoneToken = authModule.issueSystemAdminTokenWithAdminProjectScope(userId);

        PageResponse<InstanceTypeResponse> response = instanceTypeModule.listInstanceTypesByArchitect(
                keystoneToken,
                architect,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());

        authModule.invalidateSystemAdminToken(keystoneToken);
        return response;
    }

    @Override
    public void createInstanceType(String sessionId, InstanceTypeCreateRequest request) {
        String userId = sessionModule.getKeystoneUserId(sessionId);
        String keystoneToken = authModule.issueSystemAdminTokenWithAdminProjectScope(userId);

        if (!instanceTypeUtil.validateInstanceTypeName(request.getTypeName())) {
            throw new InstanceTypeException(InstanceTypeErrorCode.INVALID_INSTANCE_TYPE_NAME);
        }

        instanceTypeModule.createInstanceType(keystoneToken, request);
        authModule.invalidateSystemAdminToken(keystoneToken);
    }
}
