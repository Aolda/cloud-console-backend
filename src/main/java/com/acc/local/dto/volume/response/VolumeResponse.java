package com.acc.local.dto.volume.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class VolumeResponse {
    private String id;
    private String name;
    private List<VolumeLinkResponse> links;
}