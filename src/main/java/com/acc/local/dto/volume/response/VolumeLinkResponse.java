package com.acc.local.dto.volume.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class VolumeLinkResponse {
    private String rel;
    private String href;
}