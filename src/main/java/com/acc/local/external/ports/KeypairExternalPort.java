package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;

public interface KeypairExternalPort {

    CreateKeypairResponse createKeypair(String keystoneToken, CreateKeypairRequest request);
    void deleteKeypair(String keystoneToken, String keypairName);

    PageResponse<KeypairListResponse> callListKeypairs(String token, String marker, String direction, int limit);
}
