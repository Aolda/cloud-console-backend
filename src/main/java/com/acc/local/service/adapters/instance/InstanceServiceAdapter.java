package com.acc.local.service.adapters.instance;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.InstanceErrorCode;
import com.acc.global.exception.instance.InstanceException;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.service.modules.instance.InstanceModule;
import com.acc.local.service.modules.instance.InstanceUtil;
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

    @Override
    public PageResponse<InstanceResponse> getInstances(PageRequest page, String token) {
        /* --- token 검증 --- */

        return instanceModule.listInstances(
                token,
                "project-id",
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public void createInstance(InstanceCreateRequest request, String token) {
        /* --- token 검증 --- */

        /* --- Quota 검증 : Compute 쿼터, Volume 쿼터 --- */

        if (!instanceUtil.validateInstanceName(request.getInstanceName())) {
            throw new InstanceException(InstanceErrorCode.INVALID_INSTANCE_NAME);
        }

        if (!instanceUtil.validateAuthMethod(request.getKeypairId(), request.getPassword())) {
            throw new InstanceException(InstanceErrorCode.KEYPAIR_OR_PASSWORD_REQUIRED);
        }

        instanceModule.createInstance(token, "project-id", request);
    }


    @Override
    public void controlInstance(String instanceId, InstanceActionRequest request, String token) {
        /* --- token 검증 --- */

        instanceUtil.validateInstanceActionRequest(request);
        instanceModule.controlInstance(token, "project-id", instanceId, request);
    }
}
