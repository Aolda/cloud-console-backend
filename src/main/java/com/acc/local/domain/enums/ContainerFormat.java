package com.acc.local.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "컨테이너 포맷 타입<br>" +
        "<ul>" +
        "<li><b>BARE</b>: No container, just the raw disk image</li>" +
        "<li><b>OVF</b>: Open Virtualization Format</li>" +
        "<li><b>OVA</b>: Open Virtual Appliance</li>" +
        "<li><b>DOCKER</b>: Docker tarball</li>" +
        "<li><b>COMPRESSED</b>: Compressed tarball</li>" +
        "<li><b>AKI</b>: Amazon Kernel Image</li>" +
        "<li><b>ARI</b>: Amazon Ramdisk Image</li>" +
        "<li><b>AMI</b>: Amazon Machine Image</li>" +
        "</ul>")
public enum ContainerFormat {
    BARE,
    OVF,
    OVA,
    DOCKER,
    COMPRESSED,
    AKI,
    ARI,
    AMI
}