package com.acc.local.service.modules.volume.dto.response;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
public class CinderVolumeResponse {
    private String id;
    private String name;
    private List<CinderVolumeLinkResponse> links;
}
