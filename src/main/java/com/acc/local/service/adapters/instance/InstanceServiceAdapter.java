package com.acc.local.service.adapters.instance;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.InstanceErrorCode;
import com.acc.global.exception.instance.InstanceException;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceQuotaResponse;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.dto.project.ProjectQuotaDto;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.auth.ProjectModule;
import com.acc.local.service.modules.instance.InstanceModule;
import com.acc.local.service.modules.instance.InstanceUtil;
import com.acc.local.service.ports.InstanceServicePort;
import jakarta.mail.Quota;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class InstanceServiceAdapter implements InstanceServicePort {

    private final InstanceModule instanceModule;
    private final InstanceUtil instanceUtil;
    private final AuthModule authModule;
    private final ProjectModule projectModule;

    @Override
    public PageResponse<InstanceResponse> getInstances(PageRequest page, String userId, String projectId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);

        return instanceModule.listInstances(
                keystoneToken,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public void createInstance(InstanceCreateRequest request, String userId, String projectId) {
        // TODO:  Quota 검증

        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);

        if (!instanceUtil.validateInstanceName(request.getInstanceName())) {
            throw new InstanceException(InstanceErrorCode.INVALID_INSTANCE_NAME);
        }

        if (!instanceUtil.validateAuthMethod(request.getKeypairId(), request.getPassword())) {
            throw new InstanceException(InstanceErrorCode.KEYPAIR_OR_PASSWORD_REQUIRED);
        }

        instanceModule.createInstance(keystoneToken, projectId, request);
    }


    @Override
    public void controlInstance(String instanceId, InstanceActionRequest request, String userId, String projectId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        instanceUtil.validateInstanceActionRequest(request);
        instanceModule.controlInstance(keystoneToken, projectId, instanceId, request);
    }

    @Override
    public InstanceQuotaResponse getQuota(String userId, String projectId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);
        InstanceQuotaResponse projectComputeQuotaDetail = projectModule.getProjectComputeQuotaDetail(projectId, token);
        authModule.invalidateServiceTokensByUserId(userId);

        return projectComputeQuotaDetail;
    }
}
