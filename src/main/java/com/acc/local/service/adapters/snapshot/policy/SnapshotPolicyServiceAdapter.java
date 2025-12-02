package com.acc.local.service.adapters.snapshot.policy;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.service.modules.snapshot.policy.SnapshotPolicyModule;
import com.acc.local.service.ports.SnapshotPolicyServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Primary
@RequiredArgsConstructor
public class SnapshotPolicyServiceAdapter implements SnapshotPolicyServicePort {

    private final SnapshotPolicyModule policyModule;

    @Override
    public PageResponse<SnapshotPolicyResponse> getPolicies(PageRequest page) {
        Pageable pageable = toPageable(page);
        Page<SnapshotPolicyResponse> policies = policyModule.getPolicies(pageable);
        return toPageResponse(policies, page);
    }

    @Override
    public SnapshotPolicyResponse getPolicyDetails(Long policyId) {
        validatePolicyId(policyId);
        return policyModule.getPolicyDetails(policyId);
    }

    @Override
    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request) {
        validateRequest(request);
        return policyModule.createPolicy(request);
    }

    @Override
    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request) {
        validatePolicyId(policyId);
        validateRequest(request);
        return policyModule.updatePolicy(policyId, request);
    }

    @Override
    public void deletePolicy(Long policyId) {
        validatePolicyId(policyId);
        policyModule.deletePolicy(policyId);
    }

    @Override
    public void activatePolicy(Long policyId) {
        validatePolicyId(policyId);
        policyModule.activatePolicy(policyId);
    }

    @Override
    public void deactivatePolicy(Long policyId) {
        validatePolicyId(policyId);
        policyModule.deactivatePolicy(policyId);
    }

    @Override
    public PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page) {
        validatePolicyId(policyId);
        Pageable pageable = toPageable(page);
        Page<SnapshotTaskResponse> tasks = policyModule.getPolicyRuns(policyId, since, pageable);
        return toPageResponse(tasks, page);
    }

    private Pageable toPageable(PageRequest page) {
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

        return org.springframework.data.domain.PageRequest.of(pageNumber, size);
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page, PageRequest request) {
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

    private void validatePolicyId(Long policyId) {
        if (policyId == null || policyId <= 0) {
            throw new VolumeException(VolumeErrorCode.INVALID_POLICY_ID);
        }
    }

    private void validateRequest(SnapshotPolicyRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty() || request.getName().length() > 100) {
            throw new VolumeException(VolumeErrorCode.INVALID_POLICY_NAME);
        }
        if (request.getVolumeId() == null || request.getVolumeId().trim().isEmpty()) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
    }
}
