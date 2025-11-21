package com.acc.local.service.modules.snapshot.policy;

import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.domain.enums.IntervalType;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import com.acc.local.repository.ports.SnapshotTaskRepositoryPort;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnapshotPolicyModuleTest {

    @Mock
    private SnapshotPolicyRepositoryPort policyRepository;

    @Mock
    private SnapshotTaskRepositoryPort taskRepository;

    @InjectMocks
    private SnapshotPolicyModule policyModule;

    @Test
    @DisplayName("정책을 생성할 수 있다")
    void givenValidRequest_whenCreatePolicy_thenReturnPolicyResponse() {
        // given
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName("daily-backup");
        request.setDescription("Daily backup policy");
        request.setVolumeId("volume-1");
        request.setIntervalType(IntervalType.DAILY);
        request.setDailyTime(LocalTime.of(2, 0));

        SnapshotPolicyEntity savedEntity = SnapshotPolicyEntity.builder()
                .name("daily-backup")
                .description("Daily backup policy")
                .volumeId("volume-1")
                .intervalType(IntervalType.DAILY)
                .enabled(true)
                .dailyTime(LocalTime.of(2, 0))
                .timezone("Asia/Seoul")
                .build();
        
        // Mock saved entity with id
        SnapshotPolicyEntity savedEntityWithId = spy(savedEntity);
        when(savedEntityWithId.getId()).thenReturn(1L);

        when(policyRepository.save(any(SnapshotPolicyEntity.class))).thenAnswer(invocation -> {
            SnapshotPolicyEntity entity = invocation.getArgument(0);
            // 실제로는 DB에서 id가 생성되지만, 테스트에서는 mock으로 처리
            return savedEntityWithId;
        });

        // when
        SnapshotPolicyResponse response = policyModule.createPolicy(request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getPolicyId());
        assertEquals("daily-backup", response.getName());
        assertEquals(IntervalType.DAILY, response.getIntervalType());
        verify(policyRepository).save(any(SnapshotPolicyEntity.class));
    }

    @Test
    @DisplayName("DAILY 타입 정책 생성 시 dailyTime이 없으면 예외가 발생한다")
    void givenDailyTypeWithoutTime_whenCreatePolicy_thenThrowException() {
        // given
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName("daily-backup");
        request.setVolumeId("volume-1");
        request.setIntervalType(IntervalType.DAILY);
        // dailyTime이 null

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyModule.createPolicy(request);
        });

        assertEquals(VolumeErrorCode.INVALID_SCHEDULE_PARAMETER, exception.getErrorCode());
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("정책을 수정할 수 있다")
    void givenValidRequest_whenUpdatePolicy_thenReturnUpdatedPolicy() {
        // given
        Long policyId = 1L;
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName("updated-backup");
        request.setDescription("Updated backup policy");
        request.setIntervalType(IntervalType.WEEKLY);
        request.setWeeklyDayOfWeek(1);
        request.setWeeklyTime(LocalTime.of(3, 0));

        SnapshotPolicyEntity existingEntity = SnapshotPolicyEntity.builder()
                .name("daily-backup")
                .volumeId("volume-1")
                .intervalType(IntervalType.DAILY)
                .enabled(true)
                .build();
        
        SnapshotPolicyEntity entityWithId = spy(existingEntity);
        when(entityWithId.getId()).thenReturn(policyId);

        when(policyRepository.findById(policyId)).thenReturn(Optional.of(entityWithId));
        when(policyRepository.save(any(SnapshotPolicyEntity.class))).thenReturn(entityWithId);

        // when
        SnapshotPolicyResponse response = policyModule.updatePolicy(policyId, request);

        // then
        assertNotNull(response);
        verify(policyRepository).findById(policyId);
        verify(policyRepository).save(any(SnapshotPolicyEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 정책을 수정 시 예외가 발생한다")
    void givenNonExistentPolicy_whenUpdatePolicy_thenThrowException() {
        // given
        Long policyId = 999L;
        SnapshotPolicyRequest request = new SnapshotPolicyRequest();
        request.setName("updated-backup");
        request.setIntervalType(IntervalType.DAILY);
        request.setDailyTime(LocalTime.of(2, 0));

        when(policyRepository.findById(policyId)).thenReturn(Optional.empty());

        // when & then
        VolumeException exception = assertThrows(VolumeException.class, () -> {
            policyModule.updatePolicy(policyId, request);
        });

        assertEquals(VolumeErrorCode.POLICY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("정책을 활성화할 수 있다")
    void givenValidPolicyId_whenActivatePolicy_thenSuccess() {
        // given
        Long policyId = 1L;
        SnapshotPolicyEntity entity = SnapshotPolicyEntity.builder()
                .name("daily-backup")
                .enabled(false)
                .build();

        when(policyRepository.findById(policyId)).thenReturn(Optional.of(entity));
        when(policyRepository.save(any(SnapshotPolicyEntity.class))).thenAnswer(invocation -> {
            SnapshotPolicyEntity saved = invocation.getArgument(0);
            return saved;
        });

        // when
        policyModule.activatePolicy(policyId);

        // then
        assertTrue(entity.getEnabled());
        verify(policyRepository).findById(policyId);
        verify(policyRepository).save(entity);
    }

    @Test
    @DisplayName("정책을 비활성화할 수 있다")
    void givenValidPolicyId_whenDeactivatePolicy_thenSuccess() {
        // given
        Long policyId = 1L;
        SnapshotPolicyEntity entity = SnapshotPolicyEntity.builder()
                .name("daily-backup")
                .enabled(true)
                .build();
        
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(entity));
        when(policyRepository.save(any(SnapshotPolicyEntity.class))).thenAnswer(invocation -> {
            SnapshotPolicyEntity saved = invocation.getArgument(0);
            return saved;
        });

        // when
        policyModule.deactivatePolicy(policyId);

        // then
        assertFalse(entity.getEnabled());
        verify(policyRepository).findById(policyId);
        verify(policyRepository).save(entity);
    }
}