package com.acc.local.external.ports;

import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;

public interface KeypairExternalPort {

    CreateKeypairResponse createKeypair(String keystoneToken, CreateKeypairRequest request);
    void deleteKeypair(String keystoneToken, String keypairName);
}
