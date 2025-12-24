package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.SnapshotPolicyDocs;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.service.ports.SnapshotPolicyServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SnapshotPolicyController implements SnapshotPolicyDocs {

    private final SnapshotPolicyServicePort policyServicePort;

    @Override
    public ResponseEntity<PageResponse<SnapshotPolicyResponse>> getPolicies(
            PageRequest page,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        PageResponse<SnapshotPolicyResponse> response = policyServicePort.getPolicies(page, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> getPolicyDetails(
            Long policyId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        SnapshotPolicyResponse response = policyServicePort.getPolicyDetails(policyId, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> createPolicy(
            SnapshotPolicyRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        SnapshotPolicyResponse response = policyServicePort.createPolicy(request, userId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> updatePolicy(
            Long policyId,
            SnapshotPolicyRequest request,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        SnapshotPolicyResponse response = policyServicePort.updatePolicy(policyId, request, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletePolicy(
            Long policyId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        policyServicePort.deletePolicy(policyId, userId, projectId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deactivatePolicy(
            Long policyId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        policyServicePort.deactivatePolicy(policyId, userId, projectId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> activatePolicy(
            Long policyId,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        policyServicePort.activatePolicy(policyId, userId, projectId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PageResponse<SnapshotTaskResponse>> getPolicyRuns(
            Long policyId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            PageRequest page,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        PageResponse<SnapshotTaskResponse> response = policyServicePort.getPolicyRuns(policyId, since, page, userId, projectId);
        return ResponseEntity.ok(response);
    }
}
