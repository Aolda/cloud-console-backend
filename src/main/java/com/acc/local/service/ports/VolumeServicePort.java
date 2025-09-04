package com.acc.local.service.ports;

import com.acc.local.dto.volume.response.VolumeDetailResponse;
import com.acc.local.dto.volume.response.VolumeResponse;

public interface VolumeServicePort {
    VolumeResponse readVolume();
    VolumeDetailResponse readVolumeDetail();
}
