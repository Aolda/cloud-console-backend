package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;

import java.time.LocalDate;

public interface SnapshotPolicyServicePort {
    PageResponse<SnapshotPolicyResponse> getPolicies(PageRequest page, String userId, String projectId);
    SnapshotPolicyResponse getPolicyDetails(Long policyId, String userId, String projectId);
    SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String userId, String projectId);
    SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String userId, String projectId);
    void deletePolicy(Long policyId, String userId, String projectId);
    void activatePolicy(Long policyId, String userId, String projectId);
    void deactivatePolicy(Long policyId, String userId, String projectId);
    PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page, String userId, String projectId);
}
