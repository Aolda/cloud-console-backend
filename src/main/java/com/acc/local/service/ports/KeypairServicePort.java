package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;

public interface KeypairServicePort {

    PageResponse<KeypairListResponse> getKeypairs(String token, PageRequest page);
    CreateKeypairResponse createKeypair(String token, CreateKeypairRequest request);
    void deleteKeypair(String token, String keypairId);
}
