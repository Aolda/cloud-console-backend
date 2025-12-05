package com.acc.local.service.adapters.keypair;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.keypair.KeypairErrorCode;
import com.acc.global.exception.keypair.KeypairException;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.service.modules.auth.AuthModule;
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
    private final AuthModule authModule;

    @Override
    public PageResponse<KeypairListResponse> getKeypairs(PageRequest page, String projectId) {
        return keypairModule.getKeypairs(
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public CreateKeypairResponse createKeypair(CreateKeypairRequest request, String userId, String projectId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        // TODO : Quota 검증
        if (!keypairUtil.validateKeypairName(request.getKeypairName())) {
            throw new KeypairException(KeypairErrorCode.INVALID_KEYPAIR_NAME);
        }
        return keypairModule.createKeypair(request, keystoneToken, projectId);
    }

    @Override
    public void deleteKeypair(String keypairId, String userId, String projectId) {
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        keypairModule.deleteKeypair(keypairId, keystoneToken, projectId);
    }
}
