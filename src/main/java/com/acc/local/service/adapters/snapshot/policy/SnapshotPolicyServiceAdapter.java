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
        Pageable pageable = policyModule.toPageable(page);
        Page<SnapshotPolicyResponse> policies = policyModule.getPolicies(pageable);
        return policyModule.toPageResponse(policies, page);
    }

    @Override
    public SnapshotPolicyResponse getPolicyDetails(Long policyId) {
        policyModule.validatePolicyId(policyId);
        return policyModule.getPolicyDetails(policyId);
    }

    @Override
    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request) {
        policyModule.validateRequest(request);
        return policyModule.createPolicy(request);
    }

    @Override
    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request) {
        policyModule.validatePolicyId(policyId);
        policyModule.validateRequest(request);
        return policyModule.updatePolicy(policyId, request);
    }

    @Override
    public void deletePolicy(Long policyId) {
        policyModule.validatePolicyId(policyId);
        policyModule.deletePolicy(policyId);
    }

    @Override
    public void activatePolicy(Long policyId) {
        policyModule.validatePolicyId(policyId);
        policyModule.activatePolicy(policyId);
    }

    @Override
    public void deactivatePolicy(Long policyId) {
        policyModule.validatePolicyId(policyId);
        policyModule.deactivatePolicy(policyId);
    }

    @Override
    public PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page) {
        policyModule.validatePolicyId(policyId);
        Pageable pageable = policyModule.toPageable(page);
        Page<SnapshotTaskResponse> tasks = policyModule.getPolicyRuns(policyId, since, pageable);
        return policyModule.toPageResponse(tasks, page);
    }
}
