package com.acc.local.service.modules.volume;

import com.acc.local.dto.volume.response.VolumeDetailResponse;
import com.acc.local.dto.volume.response.VolumeLinkResponse;
import com.acc.local.dto.volume.response.VolumeResponse;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeDetailResponse;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeLinkResponse;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CinderMapper {

    public VolumeResponse toVolumeResponse(CinderVolumeResponse src) {
        if (src == null) return null;
        return VolumeResponse.of(
                src.getId(),
                src.getName(),
                mapLinks(src.getLinks())
        );
    }

    public VolumeDetailResponse toVolumeDetailResponse(CinderVolumeDetailResponse src) {
        if (src == null) return null;
        return VolumeDetailResponse.of(
                src.getId(),
                src.getStatus(),
                src.getSize(),
                src.getAvailabilityZone(),
                src.getCreatedAt(),
                src.getUpdatedAt(),
                src.getName(),
                src.getDescription(),
                src.getVolumeType(),
                src.getSnapshotId(),
                src.getSourceVolid(),
                src.getMetadata(),
                mapLinks(src.getLinks()),
                src.getUserId(),
                src.getBootable(),
                src.isEncrypted(),
                src.getReplicationStatus(),
                src.getConsistencyGroupId(),
                src.isMultiattach(),
                src.getAttachments(),
                src.getMigrationStatus(),
                src.getHost(),
                src.getTenantId(),
                src.getMigstat(),
                src.getNameId()
        );
    }

    private List<VolumeLinkResponse> mapLinks(List<CinderVolumeLinkResponse> links) {
        if (links == null) return Collections.emptyList();
        return links.stream()
                .map(l -> VolumeLinkResponse.of(l.getRel(), l.getHref()))
                .collect(Collectors.toList());
    }
}
