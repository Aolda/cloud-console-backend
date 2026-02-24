package com.acc.local.service.adapters.instance;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.InstanceErrorCode;
import com.acc.global.exception.instance.InstanceException;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceQuotaResponse;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.dto.project.quota.ProjectComputeQuotaDto;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.instance.InstanceModule;
import com.acc.local.service.modules.instance.InstanceUtil;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.InstanceServicePort;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class InstanceServiceAdapter implements InstanceServicePort {

    private final InstanceModule instanceModule;
    private final InstanceUtil instanceUtil;
    private final SessionModule sessionModule;
    private final ProjectModule projectModule;

    @Override
    public PageResponse<InstanceResponse> getInstances(PageRequest page, String sessionId, String projectId) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        return instanceModule.listInstances(
                keystoneToken,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public void createInstance(InstanceCreateRequest request, String sessionId, String projectId) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        ProjectComputeQuotaDto quota = projectModule.getProjectComputeQuotaDetail(projectId, keystoneToken);
        instanceUtil.validateQuotaForInstanceCreation(quota);

        if (!instanceUtil.validateInstanceName(request.getInstanceName())) {
            throw new InstanceException(InstanceErrorCode.INVALID_INSTANCE_NAME);
        }

        if (!instanceUtil.validateAuthMethod(request.getKeypairName(), request.getPassword())) {
            throw new InstanceException(InstanceErrorCode.KEYPAIR_OR_PASSWORD_REQUIRED);
        }

        if (!instanceUtil.validateNetworkConnection(request.getNetworkIds(), request.getInterfaceIds())) {
            throw new InstanceException(InstanceErrorCode.NETWORK_OR_INTERFACE_REQUIRED);
        }

        instanceModule.createInstance(keystoneToken, projectId, request);
    }

    @Override
    public void controlInstance(String instanceId, InstanceActionRequest request, String sessionId, String projectId) {
        String keystoneToken = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        instanceUtil.validateInstanceActionRequest(request);
        instanceModule.controlInstance(keystoneToken, projectId, instanceId, request);
    }

    @Override
    public InstanceQuotaResponse getQuota(String sessionId, String projectId) {
        if (projectId == null) {
            throw new InstanceException(InstanceErrorCode.INVALID_ACTION);
        }

        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        ProjectComputeQuotaDto projectComputeQuota = projectModule.getProjectComputeQuotaDetail(projectId, token);

        return InstanceQuotaResponse.from(projectComputeQuota);
    }
}
