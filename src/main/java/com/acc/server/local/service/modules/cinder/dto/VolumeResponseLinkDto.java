package com.acc.server.local.service.modules.cinder.dto;

import lombok.Data;

@Data
public class VolumeResponseLinkDto {
    private String rel;
    private String href;
}