package com.acc.local.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

//Glance/v2/schemas/image 와 1:1
@Schema(description = "디스크 포맷 타입<br>" +
        "<ul>" +
        "<li><b>QCOW2</b>: QEMU Copy-On-Write 2</li>" +
        "<li><b>RAW</b>: Raw disk image</li>" +
        "<li><b>VHD</b>: Virtual Hard Disk (Hyper-V)</li>" +
        "<li><b>VHDX</b>: Virtual Hard Disk Extended (Hyper-V)</li>" +
        "<li><b>VMDK</b>: VMware Virtual Machine Disk</li>" +
        "<li><b>VDI</b>: VirtualBox Disk Image</li>" +
        "<li><b>ISO</b>: ISO 9660 optical disc image</li>" +
        "<li><b>PLOOP</b>: Parallels Loop disk image</li>" +
        "<li><b>AKI</b>: Amazon Kernel Image</li>" +
        "<li><b>ARI</b>: Amazon Ramdisk Image</li>" +
        "<li><b>AMI</b>: Amazon Machine Image</li>" +
        "</ul>")
public enum DiskFormat {
    QCOW2,
    RAW,
    VHD,
    VHDX,
    VMDK,
    VDI,
    ISO,
    PLOOP,
    AKI,
    ARI,
    AMI
}