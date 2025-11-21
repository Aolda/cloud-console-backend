package com.acc.local.service.ports;

import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SnapshotPolicyServicePort {
    Page<SnapshotPolicyResponse> getPolicies(Pageable pageable, String token);
    SnapshotPolicyResponse getPolicyDetails(Long policyId, String token);
    SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String token);
    SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String token);
    void deletePolicy(Long policyId, String token);
    void activatePolicy(Long policyId, String token);
    void deactivatePolicy(Long policyId, String token);
    Page<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, Pageable pageable, String token);
}

