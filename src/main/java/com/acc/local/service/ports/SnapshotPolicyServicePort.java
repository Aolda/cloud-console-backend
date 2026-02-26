package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;

import java.time.LocalDate;

public interface SnapshotPolicyServicePort {
    PageResponse<SnapshotPolicyResponse> getPolicies(PageRequest page, String sessionId, String projectId);
    SnapshotPolicyResponse getPolicyDetails(Long policyId, String sessionId, String projectId);
    SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String sessionId, String projectId);
    SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String sessionId, String projectId);
    void deletePolicy(Long policyId, String sessionId, String projectId);
    void activatePolicy(Long policyId, String sessionId, String projectId);
    void deactivatePolicy(Long policyId, String sessionId, String projectId);
    PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page, String sessionId, String projectId);
}
