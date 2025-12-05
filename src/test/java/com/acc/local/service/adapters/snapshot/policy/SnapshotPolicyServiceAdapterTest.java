package com.acc.local.service.adapters.snapshot.policy;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.domain.enums.IntervalType;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.service.modules.snapshot.policy.SnapshotPolicyModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnapshotPolicyServiceAdapterTest {

    @Mock
    private SnapshotPolicyModule policyModule;

    @InjectMocks
    private SnapshotPolicyServiceAdapter policyServiceAdapter;

    @Test
    @DisplayName("정책 목록을 페이징하여 조회할 수 있다")
    void givenPageRequest_whenGetPolicies_thenReturnPageResponse() {
        // given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker("0");
        pageRequest.setLimit(10);

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        SnapshotPolicyResponse policy1 = SnapshotPolicyResponse.builder()
                .policyId(1L)
                .name("daily-backup")
                .volumeId("volume-1")
                .intervalType(IntervalType.DAILY)
                .enabled(true)
                .build();

        SnapshotPolicyResponse policy2 = SnapshotPolicyResponse.builder()
                .policyId(2L)
                .name("weekly-backup")
                .volumeId("volume-2")
                .intervalType(IntervalType.WEEKLY)
                .enabled(true)
                .build();

        Page<SnapshotPolicyResponse> expectedPage = new PageImpl<>(
                Arrays.asList(policy1, policy2), pageable, 2);

        PageResponse<SnapshotPolicyResponse> expectedResponse = PageResponse.<SnapshotPolicyResponse>builder()
                .contents(Arrays.asList(policy1, policy2))
                .first(true)
                .last(true)
                .size(2)
                .nextMarker(null)
                .prevMarker(null)
                .build();

        when(policyModule.toPageable(pageRequest)).thenReturn(pageable);
        when(policyModule.getPolicies(pageable)).thenReturn(expectedPage);
        when(policyModule.toPageResponse(expectedPage, pageRequest)).thenReturn(expectedResponse);

        // when
        PageResponse<SnapshotPolicyResponse> actualPage = policyServiceAdapter.getPolicies(pageRequest);

        // then
        assertNotNull(actualPage);
        assertEquals(2, actualPage.getSize());
        assertEquals("daily-backup", actualPage.getContents().get(0).getName());
        verify(policyModule).toPageable(pageRequest);
        verify(policyModule).getPolicies(pageable);
        verify(policyModule).toPageResponse(expectedPage, pageRequest);
    }

    @Test
    @DisplayName("유효한 정책 ID로 정책 상세 정보를 조회할 수 있다")
    void givenValidPolicyId_whenGetPolicyDetails_thenReturnPolicyResponse() {
        // given
        Long policyId = 1L;

        SnapshotPolicyResponse expectedPolicy = SnapshotPolicyResponse.builder()
                .policyId(policyId)
                .name("daily-backup")
                .description("Daily backup policy")
                .volumeId("volume-1")
                .intervalType(IntervalType.DAILY)
                .enabled(true)
                .dailyTime(LocalTime.of(2, 0))
                .build();

        when(policyModule.getPolicyDetails(policyId)).thenReturn(expectedPolicy);

        // when
        SnapshotPolicyResponse actualPolicy = policyServiceAdapter.getPolicyDetails(policyId);

        // then
        assertNotNull(actualPolicy);
        assertEquals(policyId, actualPolicy.getPolicyId());
        assertEquals("daily-backup", actualPolicy.getName());
        verify(policyModule).getPolicyDetails(policyId);
    }

    @Test
    @DisplayName("유효하지 않은 정책 ID로 조회 시 예외가 발생한다")
    void givenInvalidPolicyId_whenGetPolicyDetails_thenThrowException() {
        // given
        Long invalidPolicyId = 0L;

        doThrow(new VolumeException(VolumeErrorCode.INVALID_POLICY_ID))
                .when(policyModule).validatePolicyId(invalidPolicyId);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyServiceAdapter.getPolicyDetails(invalidPolicyId);
        });

        assertEquals(VolumeErrorCode.INVALID_POLICY_ID, exception.getErrorCode());
        verify(policyModule, never()).getPolicyDetails(anyLong());
    }

    @Test
    @DisplayName("유효한 요청으로 정책을 생성할 수 있다")
    void givenValidRequest_whenCreatePolicy_thenReturnCreatedPolicy() {
        // given
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName("daily-backup");
        request.setDescription("Daily backup policy");
        request.setVolumeId("volume-1");
        request.setIntervalType(IntervalType.DAILY);
        request.setDailyTime(LocalTime.of(2, 0));

        SnapshotPolicyResponse expectedPolicy = SnapshotPolicyResponse.builder()
                .policyId(1L)
                .name("daily-backup")
                .description("Daily backup policy")
                .volumeId("volume-1")
                .intervalType(IntervalType.DAILY)
                .enabled(true)
                .dailyTime(LocalTime.of(2, 0))
                .build();

        when(policyModule.createPolicy(request)).thenReturn(expectedPolicy);

        // when
        SnapshotPolicyResponse actualPolicy = policyServiceAdapter.createPolicy(request);

        // then
        assertNotNull(actualPolicy);
        assertEquals(1L, actualPolicy.getPolicyId());
        assertEquals("daily-backup", actualPolicy.getName());
        verify(policyModule).validateRequest(request);
        verify(policyModule).createPolicy(request);
    }

    @Test
    @DisplayName("유효하지 않은 이름으로 정책 생성 시 예외가 발생한다")
    void givenInvalidName_whenCreatePolicy_thenThrowException() {
        // given
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName(""); // 빈 이름
        request.setVolumeId("volume-1");
        request.setIntervalType(IntervalType.DAILY);

        doThrow(new VolumeException(VolumeErrorCode.INVALID_POLICY_NAME))
                .when(policyModule).validateRequest(request);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyServiceAdapter.createPolicy(request);
        });

        assertEquals(VolumeErrorCode.INVALID_POLICY_NAME, exception.getErrorCode());
        verify(policyModule, never()).createPolicy(any());
    }

    @Test
    @DisplayName("정책을 활성화할 수 있다")
    void givenValidPolicyId_whenActivatePolicy_thenSuccess() {
        // given
        Long policyId = 1L;

        doNothing().when(policyModule).activatePolicy(policyId);

        // when & then
        assertDoesNotThrow(() -> {
            policyServiceAdapter.activatePolicy(policyId);
        });

        verify(policyModule).activatePolicy(policyId);
    }

    @Test
    @DisplayName("정책을 비활성화할 수 있다")
    void givenValidPolicyId_whenDeactivatePolicy_thenSuccess() {
        // given
        Long policyId = 1L;

        doNothing().when(policyModule).deactivatePolicy(policyId);

        // when & then
        assertDoesNotThrow(() -> {
            policyServiceAdapter.deactivatePolicy(policyId);
        });

        verify(policyModule).deactivatePolicy(policyId);
    }
}
