package com.acc.local.service.modules.snapshot.policy;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.entity.SnapshotPolicyEntity;
import com.acc.local.entity.SnapshotTaskEntity;
import com.acc.local.repository.ports.SnapshotPolicyRepositoryPort;
import com.acc.local.repository.ports.SnapshotTaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class SnapshotPolicyModule {

    private final SnapshotPolicyRepositoryPort policyRepository;
    private final SnapshotTaskRepositoryPort taskRepository;

    public void validatePolicyId(Long policyId) {
        if (policyId == null || policyId <= 0) {
            throw new VolumeException(VolumeErrorCode.INVALID_POLICY_ID);
        }
    }

    public void validateRequest(SnapshotPolicyRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty() || request.getName().length() > 100) {
            throw new VolumeException(VolumeErrorCode.INVALID_POLICY_NAME);
        }
        if (request.getVolumeId() == null || request.getVolumeId().trim().isEmpty()) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
    }

    public Pageable toPageable(com.acc.global.common.PageRequest page) {
        int pageNumber = 0;
        int size = 10;

        if (page != null) {
            if (page.getLimit() != null) {
                size = page.getLimit();
            }
            if (page.getMarker() != null) {
                try {
                    pageNumber = Integer.parseInt(page.getMarker());
                } catch (NumberFormatException ignored) {
                    // ignore and use default pageNumber
                }
            }
        }

        return PageRequest.of(pageNumber, size);
    }


    public <T> PageResponse<T> toPageResponse(Page<T> page, com.acc.global.common.PageRequest request) {
        boolean isFirst = page.isFirst();
        boolean isLast = page.isLast();
        int size = page.getContent().size();

        int pageNumber = page.getNumber();

        String nextMarker = isLast ? null : String.valueOf(pageNumber + 1);
        String prevMarker = isFirst || pageNumber == 0 ? null : String.valueOf(pageNumber - 1);

        return PageResponse.<T>builder()
                .contents(page.getContent())
                .first(isFirst)
                .last(isLast)
                .size(size)
                .nextMarker(nextMarker)
                .prevMarker(prevMarker)
                .build();
    }

    public Page<SnapshotPolicyResponse> getPolicies(String projectId, Pageable pageable) {
        Page<SnapshotPolicyEntity> entities = policyRepository.findByProjectId(projectId, pageable);
        return entities.map(this::convertToResponse);
    }

    public SnapshotPolicyResponse getPolicyDetails(Long policyId, String projectId) {
        return policyRepository.findByIdAndProjectId(policyId, projectId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
    }

    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String projectId) {
        validateScheduleParameters(request);

        // 사용자가 설정한 시간 그대로 nextRunAt 계산
        LocalDateTime nextRunAt = calculateInitialNextRunAt(
                request.getIntervalType(),
                request.getDailyTime(),
                request.getWeeklyDayOfWeek(),
                request.getWeeklyTime(),
                request.getMonthlyDayOfMonth(),
                request.getMonthlyTime()
        );

        SnapshotPolicyEntity entity = SnapshotPolicyEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .volumeId(request.getVolumeId())
                .projectId(projectId)
                .intervalType(request.getIntervalType())
                .expirationDate(request.getExpirationDate())
                .nextRunAt(nextRunAt)
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

    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String projectId) {
        SnapshotPolicyEntity entity = policyRepository.findByIdAndProjectId(policyId, projectId)
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

    public void deletePolicy(Long policyId, String projectId) {
        SnapshotPolicyEntity entity = policyRepository.findByIdAndProjectId(policyId, projectId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
        policyRepository.deleteById(entity.getId());
    }

    public void activatePolicy(Long policyId, String projectId) {
        SnapshotPolicyEntity entity = policyRepository.findByIdAndProjectId(policyId, projectId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
        entity.activate();
        policyRepository.save(entity);
    }

    public void deactivatePolicy(Long policyId, String projectId) {
        SnapshotPolicyEntity entity = policyRepository.findByIdAndProjectId(policyId, projectId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));
        entity.deactivate();
        policyRepository.save(entity);
    }

    public Page<SnapshotTaskResponse> getPolicyRuns(Long policyId, String projectId, LocalDate since, Pageable pageable) {
        // Ensure policy belongs to the given project before exposing task history
        policyRepository.findByIdAndProjectId(policyId, projectId)
                .orElseThrow(() -> new VolumeException(VolumeErrorCode.POLICY_NOT_FOUND));

        LocalDateTime sinceDateTime = since != null 
                ? since.atStartOfDay() 
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        Page<SnapshotTaskEntity> tasks = taskRepository.findByPolicyIdAndStartedAtAfter(
                policyId, sinceDateTime, pageable);

        return tasks.map(this::convertTaskToResponse);
    }

    public void validateScheduleParameters(SnapshotPolicyRequest request) {
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

    /**
     * 정책 생성 시 초기 nextRunAt 계산
     * 사용자가 설정한 시간 그대로 다음 실행 시각을 계산한다.
     */
    private LocalDateTime calculateInitialNextRunAt(
            com.acc.local.domain.enums.IntervalType intervalType,
            LocalTime dailyTime,
            Integer weeklyDayOfWeek,
            LocalTime weeklyTime,
            Integer monthlyDayOfMonth,
            LocalTime monthlyTime
    ) {
        LocalDateTime now = LocalDateTime.now();

        return switch (intervalType) {
            case DAILY -> calculateNextDailyRun(now, dailyTime);
            case WEEKLY -> calculateNextWeeklyRun(now, weeklyDayOfWeek, weeklyTime);
            case MONTHLY -> calculateNextMonthlyRun(now, monthlyDayOfMonth, monthlyTime);
        };
    }

    /**
     * 일간 실행 시각 계산: 사용자가 설정한 시간 그대로
     */
    private LocalDateTime calculateNextDailyRun(LocalDateTime now, LocalTime targetTime) {
        LocalDateTime nextRun = now.toLocalDate().atTime(targetTime);

        // 오늘 목표 시각이 이미 지났으면 내일로
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return nextRun;
    }

    /**
     * 주간 실행 시각 계산: 사용자가 설정한 요일과 시간 그대로
     */
    private LocalDateTime calculateNextWeeklyRun(LocalDateTime now, Integer dayOfWeek, LocalTime targetTime) {
        LocalDate today = now.toLocalDate();
        java.time.DayOfWeek targetDayOfWeek = java.time.DayOfWeek.of(dayOfWeek);

        // 다음 목표 요일 찾기
        LocalDate targetDate = today;
        while (targetDate.getDayOfWeek() != targetDayOfWeek) {
            targetDate = targetDate.plusDays(1);
        }

        LocalDateTime nextRun = targetDate.atTime(targetTime);

        // 오늘이 목표 요일이지만 이미 시간이 지났으면 다음 주로
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusWeeks(1);
        }

        return nextRun;
    }

    /**
     * 월간 실행 시각 계산: 사용자가 설정한 일자와 시간 그대로
     */
    private LocalDateTime calculateNextMonthlyRun(LocalDateTime now, Integer dayOfMonth, LocalTime targetTime) {
        LocalDate today = now.toLocalDate();

        // 이번 달의 목표 일자 계산 (일자가 없으면 말일로)
        int actualDay = Math.min(dayOfMonth, today.lengthOfMonth());
        LocalDate targetDate = today.withDayOfMonth(actualDay);

        LocalDateTime nextRun = targetDate.atTime(targetTime);

        // 이미 지났으면 다음 달로
        if (now.isAfter(nextRun)) {
            targetDate = targetDate.plusMonths(1);
            // 다음 달 일자 재계산
            actualDay = Math.min(dayOfMonth, targetDate.lengthOfMonth());
            nextRun = targetDate.withDayOfMonth(actualDay).atTime(targetTime);
        }

        return nextRun;
    }

    public SnapshotPolicyResponse convertToResponse(SnapshotPolicyEntity entity) {
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

    public SnapshotTaskResponse convertTaskToResponse(SnapshotTaskEntity entity) {
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
