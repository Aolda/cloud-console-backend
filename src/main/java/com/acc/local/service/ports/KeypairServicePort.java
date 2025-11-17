package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;

public interface KeypairServicePort {

    PageResponse<KeypairListResponse> getKeypairs(PageRequest page, String projectId);
    CreateKeypairResponse createKeypair(CreateKeypairRequest request, String userId, String projectId);
    void deleteKeypair(String keypairId, String userId, String projectId);
}
