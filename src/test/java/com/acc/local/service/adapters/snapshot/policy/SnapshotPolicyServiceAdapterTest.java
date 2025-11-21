package com.acc.local.service.adapters.snapshot.policy;

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
import org.springframework.data.domain.PageRequest;
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
    void givenPageable_whenGetPolicies_thenReturnPageResponse() {
        // given
        String token = "test-token";
        Pageable pageable = PageRequest.of(0, 10);

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

        when(policyModule.getPolicies(pageable)).thenReturn(expectedPage);

        // when
        Page<SnapshotPolicyResponse> actualPage = policyServiceAdapter.getPolicies(pageable, token);

        // then
        assertNotNull(actualPage);
        assertEquals(2, actualPage.getTotalElements());
        assertEquals("daily-backup", actualPage.getContent().get(0).getName());
        verify(policyModule).getPolicies(pageable);
    }

    @Test
    @DisplayName("유효한 정책 ID로 정책 상세 정보를 조회할 수 있다")
    void givenValidPolicyId_whenGetPolicyDetails_thenReturnPolicyResponse() {
        // given
        String token = "test-token";
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
        SnapshotPolicyResponse actualPolicy = policyServiceAdapter.getPolicyDetails(policyId, token);

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
        String token = "test-token";
        Long invalidPolicyId = 0L;

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyServiceAdapter.getPolicyDetails(invalidPolicyId, token);
        });

        assertEquals(VolumeErrorCode.INVALID_POLICY_ID, exception.getErrorCode());
        verify(policyModule, never()).getPolicyDetails(anyLong());
    }

    @Test
    @DisplayName("유효한 요청으로 정책을 생성할 수 있다")
    void givenValidRequest_whenCreatePolicy_thenReturnCreatedPolicy() {
        // given
        String token = "test-token";
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
        SnapshotPolicyResponse actualPolicy = policyServiceAdapter.createPolicy(request, token);

        // then
        assertNotNull(actualPolicy);
        assertEquals(1L, actualPolicy.getPolicyId());
        assertEquals("daily-backup", actualPolicy.getName());
        verify(policyModule).createPolicy(request);
    }

    @Test
    @DisplayName("유효하지 않은 이름으로 정책 생성 시 예외가 발생한다")
    void givenInvalidName_whenCreatePolicy_thenThrowException() {
        // given
        String token = "test-token";
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName(""); // 빈 이름
        request.setVolumeId("volume-1");
        request.setIntervalType(IntervalType.DAILY);

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyServiceAdapter.createPolicy(request, token);
        });

        assertEquals(VolumeErrorCode.INVALID_POLICY_NAME, exception.getErrorCode());
        verify(policyModule, never()).createPolicy(any());
    }

    @Test
    @DisplayName("정책을 활성화할 수 있다")
    void givenValidPolicyId_whenActivatePolicy_thenSuccess() {
        // given
        String token = "test-token";
        Long policyId = 1L;

        doNothing().when(policyModule).activatePolicy(policyId);

        // when & then
        assertDoesNotThrow(() -> {
            policyServiceAdapter.activatePolicy(policyId, token);
        });

        verify(policyModule).activatePolicy(policyId);
    }

    @Test
    @DisplayName("정책을 비활성화할 수 있다")
    void givenValidPolicyId_whenDeactivatePolicy_thenSuccess() {
        // given
        String token = "test-token";
        Long policyId = 1L;

        doNothing().when(policyModule).deactivatePolicy(policyId);

        // when & then
        assertDoesNotThrow(() -> {
            policyServiceAdapter.deactivatePolicy(policyId, token);
        });

        verify(policyModule).deactivatePolicy(policyId);
    }
}

