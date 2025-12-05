package com.acc.local.service.adapters.snapshot.policy;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.volume.VolumeModule;
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
    private final AuthModule authModule;
    private final VolumeModule volumeModule;

    @Override
    public PageResponse<SnapshotPolicyResponse> getPolicies(PageRequest page, String userId, String projectId) {
        Pageable pageable = policyModule.toPageable(page);
        Page<SnapshotPolicyResponse> policies = policyModule.getPolicies(projectId, pageable);
        return policyModule.toPageResponse(policies, page);
    }

    @Override
    public SnapshotPolicyResponse getPolicyDetails(Long policyId, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        return policyModule.getPolicyDetails(policyId, projectId);
    }

    @Override
    public SnapshotPolicyResponse createPolicy(SnapshotPolicyRequest request, String userId, String projectId) {
        policyModule.validateRequest(request);
        // 볼륨이 해당 프로젝트에 속해있는지 검증
        String keystoneToken = authModule.issueProjectScopeToken(projectId, userId);
        // 존재하지 않거나 다른 프로젝트인 경우 VolumeModule 내부에서 예외를 던짐
        volumeModule.getVolumeDetails(keystoneToken, projectId, request.getVolumeId());

        return policyModule.createPolicy(request, projectId);
    }

    @Override
    public SnapshotPolicyResponse updatePolicy(Long policyId, SnapshotPolicyRequest request, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        policyModule.validateRequest(request);
        return policyModule.updatePolicy(policyId, request, projectId);
    }

    @Override
    public void deletePolicy(Long policyId, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        policyModule.deletePolicy(policyId, projectId);
    }

    @Override
    public void activatePolicy(Long policyId, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        policyModule.activatePolicy(policyId, projectId);
    }

    @Override
    public void deactivatePolicy(Long policyId, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        policyModule.deactivatePolicy(policyId, projectId);
    }

    @Override
    public PageResponse<SnapshotTaskResponse> getPolicyRuns(Long policyId, LocalDate since, PageRequest page, String userId, String projectId) {
        policyModule.validatePolicyId(policyId);
        Pageable pageable = policyModule.toPageable(page);
        Page<SnapshotTaskResponse> tasks = policyModule.getPolicyRuns(policyId, projectId, since, pageable);
        return policyModule.toPageResponse(tasks, page);
    }
}
