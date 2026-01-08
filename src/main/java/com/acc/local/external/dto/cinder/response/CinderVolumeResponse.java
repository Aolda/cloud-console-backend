package com.acc.local.external.dto.cinder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinderVolumeResponse {

    private CinderVolumesResponse.Volume volume;
}