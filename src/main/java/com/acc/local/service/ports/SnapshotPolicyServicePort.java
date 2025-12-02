package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;

import java.time.LocalDate;

public interface SnapshotPolicyServicePort {
    PageResponse<SnapshotPolicyResponse> getPolicies(PageRequest page);
    SnapshotPolicyResponse getPolicyDetails(Long policyId);
    SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request);
    SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request);
    void deletePolicy(Long policyId);
    void activatePolicy(Long policyId);
    void deactivatePolicy(Long policyId);
    PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page);
}
