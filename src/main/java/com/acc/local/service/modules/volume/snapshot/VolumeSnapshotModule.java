package com.acc.local.service.modules.volume.snapshot;
import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.external.ports.VolumeSnapshotExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VolumeSnapshotModule {
    private final VolumeSnapshotExternalPort volumeSnapshotExternalPort;

    public PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String projectId, String token) {

        return volumeSnapshotExternalPort.callListSnapshots(
                token,
                projectId,
                page.getMarker(),
                (page.getLimit() != null) ? page.getLimit() : 10
        );
    }


    public VolumeSnapshotResponse getSnapshotDetails(String token, String projectId, String snapshotId) {
        return volumeSnapshotExternalPort.callGetSnapshotDetails(token, projectId, snapshotId);
    }

    public ResponseEntity<Void> deleteSnapshot(String token, String projectId, String snapshotId) {
        return volumeSnapshotExternalPort.callDeleteSnapshot(token,  projectId,snapshotId);
    }

    public VolumeSnapshotResponse createSnapshot(String token,String projectId, VolumeSnapshotRequest request) {
        return volumeSnapshotExternalPort.callCreateSnapshot(token,  projectId, request);
    }



}
