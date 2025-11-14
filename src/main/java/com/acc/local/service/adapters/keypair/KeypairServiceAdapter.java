package com.acc.local.service.adapters.keypair;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.keypair.KeypairErrorCode;
import com.acc.global.exception.keypair.KeypairException;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.service.modules.keypair.KeypairModule;
import com.acc.local.service.modules.keypair.KeypairUtil;
import com.acc.local.service.ports.KeypairServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class KeypairServiceAdapter implements KeypairServicePort {

    private final KeypairModule keypairModule;
    private final KeypairUtil keypairUtil;

    @Override
    public PageResponse<KeypairListResponse> getKeypairs(String token, PageRequest page) {
        String projectId = "project-id";
        return keypairModule.getKeypairs(
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public CreateKeypairResponse createKeypair(String token, CreateKeypairRequest request) {
        // TODO : Quota 검증

        if (!keypairUtil.validateKeypairName(request.getKeypairName())) {
            throw new KeypairException(KeypairErrorCode.INVALID_KEYPAIR_NAME);
        }
        String keystoneToken = "keystone-token";
        String projectId = "project-id";
        return keypairModule.createKeypair(keystoneToken, projectId, request);
    }

    @Override
    public void deleteKeypair(String token, String keypairId) {
        String keystoneToken = "keystone-token";
        String projectId = "project-id";
        keypairModule.deleteKeypair(keystoneToken, projectId, keypairId);
    }
}
