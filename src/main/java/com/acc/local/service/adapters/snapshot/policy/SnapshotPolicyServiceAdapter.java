package com.acc.local.service.adapters.snapshot.policy;

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
    public Page<SnapshotPolicyResponse> getPolicies(Pageable pageable, String token) {
        return policyModule.getPolicies(pageable);
    }

    @Override
    public SnapshotPolicyResponse getPolicyDetails(Long policyId, String token) {
        validatePolicyId(policyId);
        return policyModule.getPolicyDetails(policyId);
    }

    @Override
    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String token) {
        validateRequest(request);
        return policyModule.createPolicy(request);
    }

    @Override
    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String token) {
        validatePolicyId(policyId);
        validateRequest(request);
        return policyModule.updatePolicy(policyId, request);
    }

    @Override
    public void deletePolicy(Long policyId, String token) {
        validatePolicyId(policyId);
        policyModule.deletePolicy(policyId);
    }

    @Override
    public void activatePolicy(Long policyId, String token) {
        validatePolicyId(policyId);
        policyModule.activatePolicy(policyId);
    }

    @Override
    public void deactivatePolicy(Long policyId, String token) {
        validatePolicyId(policyId);
        policyModule.deactivatePolicy(policyId);
    }

    @Override
    public Page<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, Pageable pageable, String token) {
        validatePolicyId(policyId);
        return policyModule.getPolicyRuns(policyId, since, pageable);
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

