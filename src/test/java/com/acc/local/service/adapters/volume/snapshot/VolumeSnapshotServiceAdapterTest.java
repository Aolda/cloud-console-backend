package com.acc.local.service.adapters.volume.snapshot;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotModule;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolumeSnapshotServiceAdapterTest {

    @Mock
    private VolumeSnapshotModule volumeSnapshotModule;

    @Mock
    private VolumeSnapshotUtil volumeSnapshotUtil;

    @InjectMocks
    private VolumeSnapshotServiceAdapter volumeSnapshotServiceAdapter;

    @Test
    @DisplayName("스냅샷 목록을 페이징하여 조회할 수 있다")
    void givenPageRequest_whenGetSnapshots_thenReturnPageResponse() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        PageRequest pageRequest = new PageRequest();
        pageRequest.setLimit(10);
        pageRequest.setMarker(null);

        List<VolumeSnapshotResponse> snapshots = Arrays.asList(
                VolumeSnapshotResponse.builder()
                        .snapshotId("snapshot-1")
                        .name("test-snapshot-1")
                        .sizeGb(10)
                        .status("available")
                        .sourceVolumeId("volume-1")
                        .build(),
                VolumeSnapshotResponse.builder()
                        .snapshotId("snapshot-2")
                        .name("test-snapshot-2")
                        .sizeGb(20)
                        .status("available")
                        .sourceVolumeId("volume-2")
                        .build()
        );

        PageResponse<VolumeSnapshotResponse> expectedResponse = PageResponse.<VolumeSnapshotResponse>builder()
                .contents(snapshots)
                .size(2)
                .first(true)
                .last(true)
                .build();

        when(volumeSnapshotModule.getSnapshots(eq(pageRequest), eq(projectId), eq(keystoneToken)))
                .thenReturn(expectedResponse);

        // when
        PageResponse<VolumeSnapshotResponse> actualResponse = volumeSnapshotServiceAdapter.getSnapshots(pageRequest, projectId, keystoneToken);

        // then
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.getSize());
        assertEquals("snapshot-1", actualResponse.getContents().get(0).getSnapshotId());
        verify(volumeSnapshotModule).getSnapshots(eq(pageRequest), eq(projectId), eq(keystoneToken));
    }

    @Test
    @DisplayName("유효한 스냅샷 ID로 스냅샷 상세 정보를 조회할 수 있다")
    void givenValidSnapshotId_whenGetSnapshotDetails_thenReturnSnapshotResponse() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String snapshotId = "e1f2a3b4-c5d6-e7f8-a9b0-c1d2e3f4a5b6";

        VolumeSnapshotResponse expectedSnapshot = VolumeSnapshotResponse.builder()
                .snapshotId(snapshotId)
                .name("test-snapshot")
                .sizeGb(10)
                .status("available")
                .sourceVolumeId("volume-1")
                .createdAt("2025-01-01T00:00:00.000000")
                .build();

        when(volumeSnapshotUtil.validateSnapshotId(snapshotId)).thenReturn(true);
        when(volumeSnapshotModule.getSnapshotDetails(eq(keystoneToken), eq(projectId), eq(snapshotId)))
                .thenReturn(expectedSnapshot);

        // when
        VolumeSnapshotResponse actualSnapshot = volumeSnapshotServiceAdapter.getSnapshotDetails(projectId, keystoneToken, snapshotId);

        // then
        assertNotNull(actualSnapshot);
        assertEquals(snapshotId, actualSnapshot.getSnapshotId());
        assertEquals("test-snapshot", actualSnapshot.getName());
        verify(volumeSnapshotUtil).validateSnapshotId(snapshotId);
        verify(volumeSnapshotModule).getSnapshotDetails(eq(keystoneToken), eq(projectId), eq(snapshotId));
    }

    @Test
    @DisplayName("유효하지 않은 스냅샷 ID로 조회 시 예외가 발생한다")
    void givenInvalidSnapshotId_whenGetSnapshotDetails_thenThrowException() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String invalidSnapshotId = "invalid-id";

        when(volumeSnapshotUtil.validateSnapshotId(invalidSnapshotId)).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeSnapshotServiceAdapter.getSnapshotDetails(projectId, keystoneToken, invalidSnapshotId);
        });

        assertEquals(VolumeErrorCode.INVALID_SNAPSHOT_ID, exception.getErrorCode());
        verify(volumeSnapshotUtil).validateSnapshotId(invalidSnapshotId);
        verify(volumeSnapshotModule, never()).getSnapshotDetails(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("유효한 요청으로 스냅샷을 생성할 수 있다")
    void givenValidRequest_whenCreateSnapshot_thenReturnCreatedSnapshot() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        VolumeSnapshotRequest request = new VolumeSnapshotRequest();
        request.setSourceVolumeId("b8f6a3b2-9d3a-4a6e-8b1e-2e4a6d8c2e1f");
        request.setName("my-daily-snapshot");

        VolumeSnapshotResponse expectedSnapshot = VolumeSnapshotResponse.builder()
                .snapshotId("new-snapshot-id")
                .name("my-daily-snapshot")
                .sizeGb(10)
                .status("creating")
                .sourceVolumeId("b8f6a3b2-9d3a-4a6e-8b1e-2e4a6d8c2e1f")
                .build();

        when(volumeSnapshotUtil.validateVolumeId(request.getSourceVolumeId())).thenReturn(true);
        when(volumeSnapshotUtil.validateSnapshotName(request.getName())).thenReturn(true);
        when(volumeSnapshotModule.createSnapshot(eq(keystoneToken), eq(projectId), eq(request)))
                .thenReturn(expectedSnapshot);

        // when
        VolumeSnapshotResponse actualSnapshot = volumeSnapshotServiceAdapter.createSnapshot(projectId, keystoneToken, request);

        // then
        assertNotNull(actualSnapshot);
        assertEquals("new-snapshot-id", actualSnapshot.getSnapshotId());
        assertEquals("my-daily-snapshot", actualSnapshot.getName());
        verify(volumeSnapshotUtil).validateVolumeId(request.getSourceVolumeId());
        verify(volumeSnapshotUtil).validateSnapshotName(request.getName());
        verify(volumeSnapshotModule).createSnapshot(eq(keystoneToken), eq(projectId), eq(request));
    }

    @Test
    @DisplayName("유효하지 않은 볼륨 ID로 스냅샷 생성 시 예외가 발생한다")
    void givenInvalidVolumeId_whenCreateSnapshot_thenThrowException() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        VolumeSnapshotRequest request = new VolumeSnapshotRequest();
        request.setSourceVolumeId("invalid-volume-id");
        request.setName("my-snapshot");

        when(volumeSnapshotUtil.validateVolumeId("invalid-volume-id")).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeSnapshotServiceAdapter.createSnapshot(projectId, keystoneToken, request);
        });

        assertEquals(VolumeErrorCode.INVALID_VOLUME_ID, exception.getErrorCode());
        verify(volumeSnapshotUtil).validateVolumeId("invalid-volume-id");
        verify(volumeSnapshotModule, never()).createSnapshot(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("유효한 스냅샷 ID로 스냅샷을 삭제할 수 있다")
    void givenValidSnapshotId_whenDeleteSnapshot_thenReturnSuccessResponse() {
        // given
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String snapshotId = "e1f2a3b4-c5d6-e7f8-a9b0-c1d2e3f4a5b6";

        when(volumeSnapshotUtil.validateSnapshotId(snapshotId)).thenReturn(true);
        when(volumeSnapshotModule.deleteSnapshot(eq(keystoneToken), eq(projectId), eq(snapshotId)))
                .thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).build());

        // when
        ResponseEntity<Void> response = volumeSnapshotServiceAdapter.deleteSnapshot(projectId, keystoneToken, snapshotId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(volumeSnapshotUtil).validateSnapshotId(snapshotId);
        verify(volumeSnapshotModule).deleteSnapshot(eq(keystoneToken), eq(projectId), eq(snapshotId));
    }
}

