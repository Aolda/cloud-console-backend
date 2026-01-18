package com.acc.local.external.ports;

import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairSyncDto;

import java.util.List;

public interface KeypairExternalPort {

    CreateKeypairResponse createKeypair(String keystoneToken, CreateKeypairRequest request);
    void deleteKeypair(String keystoneToken, String keypairName);
    List<KeypairSyncDto> listKeypairsByUser(String keystoneToken);
}
