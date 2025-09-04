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

    public VolumeResponse readVolume(){
        String token = "getToken";
        CinderVolumeResponse cinderVolumeResponse = cinderFetcher.fetchVolumes(token);
        return cinderMapper.toVolumeResponse(cinderVolumeResponse);
    }

    public VolumeDetailResponse readVolumeDetail(){
        String token = "getToken";
        CinderVolumeDetailResponse cinderVolumeDetailResponse = cinderFetcher.fetchVolumesDetail(token);
        return cinderMapper.toVolumeDetailResponse(cinderVolumeDetailResponse);
    }
}
