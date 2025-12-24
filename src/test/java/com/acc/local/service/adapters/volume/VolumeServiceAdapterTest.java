package com.acc.local.service.adapters.volume;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.volume.VolumeModule;
import com.acc.local.service.modules.volume.VolumeUtil;
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
class VolumeServiceAdapterTest {

    @Mock
    private VolumeModule volumeModule;

    @Mock
    private VolumeUtil volumeUtil;

    @Mock
    private AuthModule authModule;

    @InjectMocks
    private VolumeServiceAdapter volumeServiceAdapter;

    @Test
    @DisplayName("볼륨 목록을 페이징하여 조회할 수 있다")
    void givenPageRequest_whenGetVolumes_thenReturnPageResponse() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        PageRequest pageRequest = new PageRequest();
        pageRequest.setLimit(10);
        pageRequest.setMarker(null);

        List<VolumeResponse> volumes = Arrays.asList(
                VolumeResponse.builder()
                        .volumeId("volume-1")
                        .name("test-volume-1")
                        .size(10)
                        .status("available")
                        .build(),
                VolumeResponse.builder()
                        .volumeId("volume-2")
                        .name("test-volume-2")
                        .size(20)
                        .status("available")
                        .build()
        );

        PageResponse<VolumeResponse> expectedResponse = PageResponse.<VolumeResponse>builder()
                .contents(volumes)
                .size(2)
                .first(true)
                .last(true)
                .build();

        when(authModule.issueProjectScopeToken(projectId, userId)).thenReturn(keystoneToken);
        when(volumeModule.getVolumes(eq(pageRequest), eq(projectId), eq(keystoneToken)))
                .thenReturn(expectedResponse);

        // when
        PageResponse<VolumeResponse> actualResponse = volumeServiceAdapter.getVolumes(pageRequest, userId, projectId);

        // then
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.getSize());
        assertEquals("volume-1", actualResponse.getContents().get(0).getVolumeId());
        assertEquals("volume-2", actualResponse.getContents().get(1).getVolumeId());
        verify(volumeModule).getVolumes(eq(pageRequest), eq(projectId), eq(keystoneToken));
    }

    @Test
    @DisplayName("유효한 볼륨 ID로 볼륨 상세 정보를 조회할 수 있다")
    void givenValidVolumeId_whenGetVolumeDetails_thenReturnVolumeResponse() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String volumeId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

        VolumeResponse expectedVolume = VolumeResponse.builder()
                .volumeId(volumeId)
                .name("test-volume")
                .size(10)
                .status("available")
                .volumeType("__DEFAULT__")
                .description("Test volume")
                .availabilityZone("nova")
                .createdAt("2025-01-01T00:00:00.000000")
                .bootable("false")
                .build();

        when(authModule.issueProjectScopeToken(projectId, userId)).thenReturn(keystoneToken);
        when(volumeUtil.validateVolumeId(volumeId)).thenReturn(true);
        when(volumeModule.getVolumeDetails(eq(keystoneToken), eq(projectId), eq(volumeId)))
                .thenReturn(expectedVolume);

        // when
        VolumeResponse actualVolume = volumeServiceAdapter.getVolumeDetails(userId, projectId, volumeId);

        // then
        assertNotNull(actualVolume);
        assertEquals(volumeId, actualVolume.getVolumeId());
        assertEquals("test-volume", actualVolume.getName());
        assertEquals(10, actualVolume.getSize());
        verify(volumeUtil).validateVolumeId(volumeId);
        verify(volumeModule).getVolumeDetails(eq(keystoneToken), eq(projectId), eq(volumeId));
    }

    @Test
    @DisplayName("유효하지 않은 볼륨 ID로 조회 시 예외가 발생한다")
    void givenInvalidVolumeId_whenGetVolumeDetails_thenThrowException() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String invalidVolumeId = "invalid-id";

        when(authModule.issueProjectScopeToken(projectId, userId)).thenReturn(keystoneToken);
        when(volumeUtil.validateVolumeId(invalidVolumeId)).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeServiceAdapter.getVolumeDetails(userId, projectId, invalidVolumeId);
        });

        assertEquals(VolumeErrorCode.INVALID_VOLUME_ID, exception.getErrorCode());
        verify(volumeUtil).validateVolumeId(invalidVolumeId);
        verify(volumeModule, never()).getVolumeDetails(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("유효한 요청으로 볼륨을 생성할 수 있다")
    void givenValidRequest_whenCreateVolume_thenReturnCreatedVolume() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        VolumeRequest request = new VolumeRequest();
        request.setName("new-volume");
        request.setSize(10);
        request.setVolumeType("__DEFAULT__");
        request.setDescription("New test volume");

        VolumeResponse expectedVolume = VolumeResponse.builder()
                .volumeId("new-volume-id")
                .name("new-volume")
                .size(10)
                .status("creating")
                .volumeType("__DEFAULT__")
                .description("New test volume")
                .build();

        when(authModule.issueProjectScopeToken(projectId, userId)).thenReturn(keystoneToken);
        when(volumeUtil.validateVolumeSize(10)).thenReturn(true);
        when(volumeUtil.validateVolumeName("new-volume")).thenReturn(true);
        when(volumeModule.createVolume(eq(keystoneToken), eq(projectId), eq(request)))
                .thenReturn(expectedVolume);

        // when
        VolumeResponse actualVolume = volumeServiceAdapter.createVolume(userId, projectId, request);

        // then
        assertNotNull(actualVolume);
        assertEquals("new-volume-id", actualVolume.getVolumeId());
        assertEquals("new-volume", actualVolume.getName());
        assertEquals(10, actualVolume.getSize());
        verify(volumeUtil).validateVolumeSize(10);
        verify(volumeUtil).validateVolumeName("new-volume");
        verify(volumeModule).createVolume(eq(keystoneToken), eq(projectId), eq(request));
    }

    @Test
    @DisplayName("유효하지 않은 크기로 볼륨 생성 시 예외가 발생한다")
    void givenInvalidSize_whenCreateVolume_thenThrowException() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        VolumeRequest request = new VolumeRequest();
        request.setName("new-volume");
        request.setSize(0);

        when(volumeUtil.validateVolumeSize(0)).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeServiceAdapter.createVolume(userId, projectId, request);
        });

        assertEquals(VolumeErrorCode.INVALID_VOLUME_SIZE, exception.getErrorCode());
        verify(volumeUtil).validateVolumeSize(0);
        verify(volumeModule, never()).createVolume(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("유효하지 않은 이름으로 볼륨 생성 시 예외가 발생한다")
    void givenInvalidName_whenCreateVolume_thenThrowException() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        VolumeRequest request = new VolumeRequest();
        request.setName("!invalid-name");
        request.setSize(10);

        when(volumeUtil.validateVolumeSize(10)).thenReturn(true);
        when(volumeUtil.validateVolumeName("!invalid-name")).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeServiceAdapter.createVolume(userId, projectId, request);
        });

        assertEquals(VolumeErrorCode.INVALID_VOLUME_NAME, exception.getErrorCode());
        verify(volumeUtil).validateVolumeSize(10);
        verify(volumeUtil).validateVolumeName("!invalid-name");
        verify(volumeModule, never()).createVolume(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("유효한 볼륨 ID로 볼륨을 삭제할 수 있다")
    void givenValidVolumeId_whenDeleteVolume_thenReturnSuccessResponse() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String volumeId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

        when(authModule.issueProjectScopeToken(projectId, userId)).thenReturn(keystoneToken);
        when(volumeUtil.validateVolumeId(volumeId)).thenReturn(true);
        when(volumeModule.deleteVolume(eq(keystoneToken), eq(projectId), eq(volumeId)))
                .thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).build());

        // when
        ResponseEntity<Void> response = volumeServiceAdapter.deleteVolume(userId, projectId, volumeId);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(volumeUtil).validateVolumeId(volumeId);
        verify(volumeModule).deleteVolume(eq(keystoneToken), eq(projectId), eq(volumeId));
    }

    @Test
    @DisplayName("유효하지 않은 볼륨 ID로 삭제 시 예외가 발생한다")
    void givenInvalidVolumeId_whenDeleteVolume_thenThrowException() {
        // given
        String userId = "test-user-id";
        String projectId = "test-project-id";
        String keystoneToken = "test-keystone-token";
        String invalidVolumeId = "invalid-id";

        when(volumeUtil.validateVolumeId(invalidVolumeId)).thenReturn(false);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            volumeServiceAdapter.deleteVolume(userId, projectId, invalidVolumeId);
        });

        assertEquals(VolumeErrorCode.INVALID_VOLUME_ID, exception.getErrorCode());
        verify(volumeUtil).validateVolumeId(invalidVolumeId);
        verify(volumeModule, never()).deleteVolume(anyString(), anyString(), anyString());
    }
}
