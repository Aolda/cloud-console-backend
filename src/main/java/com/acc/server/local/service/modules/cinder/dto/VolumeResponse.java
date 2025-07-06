package com.acc.server.local.service.modules.cinder.dto;

import lombok.Data;

import java.util.List;

@Data
public class VolumeResponse {
    private String id;
    private String name;
    private List<VolumeResponseLinkDto> links;
}
