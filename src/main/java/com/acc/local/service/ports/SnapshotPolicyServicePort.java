package com.acc.local.service.ports;

import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SnapshotPolicyServicePort {
    Page<SnapshotPolicyResponse> getPolicies(Pageable pageable);
    SnapshotPolicyResponse getPolicyDetails(Long policyId);
    SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request);
    SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request);
    void deletePolicy(Long policyId);
    void activatePolicy(Long policyId);
    void deactivatePolicy(Long policyId);
    Page<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, Pageable pageable);
}
