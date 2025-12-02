package com.acc.local.service.modules.snapshot.policy;

import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.domain.enums.IntervalType;
import com.acc.local.domain.enums.TaskStatus;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.entity.SnapshotTaskEntity;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import com.acc.local.repository.ports.SnapshotTaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SnapshotPolicyModule {

    private final SnapshotPolicyRepositoryPort policyRepository;
    private final SnapshotTaskRepositoryPort taskRepository;

    public Page<SnapshotPolicyResponse> getPolicies(Pageable pageable) {
        Page<SnapshotPolicyEntity> entities = policyRepository.findAll(pageable);
        return entities.map(this::convertToResponse);
    }

    public SnapshotPolicyResponse getPolicyDetails(Long policyId) {
        return policyRepository.findById(policyId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
    }

    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request) {
        validateScheduleParameters(request);

        SnapshotPolicyEntity entity = SnapshotPolicyEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .volumeId(request.getVolumeId())
                .intervalType(request.getIntervalType())
                .expirationDate(request.getExpirationDate())
                .dailyTime(request.getDailyTime())
                .weeklyDayOfWeek(request.getWeeklyDayOfWeek())
                .weeklyTime(request.getWeeklyTime())
                .monthlyDayOfMonth(request.getMonthlyDayOfMonth())
                .monthlyTime(request.getMonthlyTime())
                .timezone(request.getTimezone())
                .build();

        SnapshotPolicyEntity saved = policyRepository.save(entity);
        return convertToResponse(saved);
    }

    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request) {
        SnapshotPolicyEntity entity = policyRepository.findById(policyId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));

        validateScheduleParameters(request);

        entity.update(
                request.getName(),
                request.getDescription(),
                request.getIntervalType(),
                request.getExpirationDate(),
                request.getDailyTime(),
                request.getWeeklyDayOfWeek(),
                request.getWeeklyTime(),
                request.getMonthlyDayOfMonth(),
                request.getMonthlyTime()
        );

        SnapshotPolicyEntity updated = policyRepository.save(entity);
        return convertToResponse(updated);
    }

    public void deletePolicy(Long policyId) {
        if (!policyRepository.findById(policyId).isPresent()) {
            throw new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND);
        }
        policyRepository.deleteById(policyId);
    }

    public void activatePolicy(Long policyId) {
        SnapshotPolicyEntity entity = policyRepository.findById(policyId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
        entity.activate();
        policyRepository.save(entity);
    }

    public void deactivatePolicy(Long policyId) {
        SnapshotPolicyEntity entity = policyRepository.findById(policyId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
        entity.deactivate();
        policyRepository.save(entity);
    }

    public Page<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, Pageable pageable) {
        LocalDateTime sinceDateTime = since != null 
                ? since.atStartOfDay() 
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        Page<SnapshotTaskEntity> tasks = taskRepository.findByPolicyIdAndStartedAtAfter(
                policyId, sinceDateTime, pageable);

        return tasks.map(this::convertTaskToResponse);
    }

    private void validateScheduleParameters(SnapshotPolicyRequest request) {
        if (request.getIntervalType() == null) {
            throw new VolumeException(VolumeErrorCode.INVALID_INTERVAL_TYPE);
        }

        switch (request.getIntervalType()) {
            case DAILY:
                if (request.getDailyTime() == null) {
                    throw new VolumeException(VolumeErrorCode.INVALID_SCHEDULE_PARAMETER);
                }
                break;
            case WEEKLY:
                if (request.getWeeklyDayOfWeek() == null || request.getWeeklyTime() == null) {
                    throw new VolumeException(VolumeErrorCode.INVALID_SCHEDULE_PARAMETER);
                }
                break;
            case MONTHLY:
                if (request.getMonthlyDayOfMonth() == null || request.getMonthlyTime() == null) {
                    throw new VolumeException(VolumeErrorCode.INVALID_SCHEDULE_PARAMETER);
                }
                break;
        }
    }

    private SnapshotPolicyResponse convertToResponse(SnapshotPolicyEntity entity) {
        return SnapshotPolicyResponse.builder()
                .policyId(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .volumeId(entity.getVolumeId())
                .intervalType(entity.getIntervalType())
                .enabled(entity.getEnabled())
                .expirationDate(entity.getExpirationDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dailyTime(entity.getDailyTime())
                .weeklyDayOfWeek(entity.getWeeklyDayOfWeek())
                .weeklyTime(entity.getWeeklyTime())
                .monthlyDayOfMonth(entity.getMonthlyDayOfMonth())
                .monthlyTime(entity.getMonthlyTime())
                .timezone(entity.getTimezone())
                .build();
    }

    private SnapshotTaskResponse convertTaskToResponse(SnapshotTaskEntity entity) {
        return SnapshotTaskResponse.builder()
                .taskId(entity.getId())
                .policyId(entity.getPolicyId())
                .volumeId(entity.getVolumeId())
                .snapshotId(entity.getSnapshotId())
                .policyNameAtExecution(entity.getPolicyNameAtExecution())
                .intervalTypeAtExecution(entity.getIntervalTypeAtExecution())
                .status(entity.getStatus())
                .startedAt(entity.getStartedAt())
                .finishedAt(entity.getFinishedAt())
                .build();
    }
}
