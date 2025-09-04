package com.acc.local.service.adapters.volume;

import com.acc.local.dto.volume.response.VolumeDetailResponse;
import com.acc.local.dto.volume.response.VolumeResponse;
import com.acc.local.service.modules.volume.CinderFetcher;
import com.acc.local.service.modules.volume.CinderMapper;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeDetailResponse;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeResponse;
import com.acc.local.service.ports.VolumeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VolumeServiceAdaptor implements VolumeServicePort {
    private final CinderFetcher cinderFetcher;
    private final CinderMapper cinderMapper;

    public VolumeResponse readVolume(String keystoneToken){
        String validKeystoneToken = validateToken(keystoneToken);
        CinderVolumeResponse cinderVolumeResponse = cinderFetcher.fetchVolumes(validKeystoneToken);
        return cinderMapper.toVolumeResponse(cinderVolumeResponse);
    }

    public VolumeDetailResponse readVolumeDetail(String keystoneToken){
        String validKeystoneToken = validateToken(keystoneToken);
        CinderVolumeDetailResponse cinderVolumeDetailResponse = cinderFetcher.fetchVolumesDetail(validKeystoneToken);
        return cinderMapper.toVolumeDetailResponse(cinderVolumeDetailResponse);
    }

    private String validateToken(String keystoneToken) {
        //예외 발생 메서드
        if(keystoneToken == null || keystoneToken.isEmpty()) {
            throw new IllegalArgumentException("keystoneToken cannot be null or empty");
        } return keystoneToken;
    }
}
