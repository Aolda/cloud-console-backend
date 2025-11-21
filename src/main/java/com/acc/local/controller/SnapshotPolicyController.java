package com.acc.local.controller;

import com.acc.local.controller.docs.SnapshotPolicyDocs;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import com.acc.local.service.ports.SnapshotPolicyServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SnapshotPolicyController implements SnapshotPolicyDocs {

    private final SnapshotPolicyServicePort policyServicePort;

    @Override
    public ResponseEntity<Page<SnapshotPolicyResponse>> getPolicies(
            String token,
            Pageable pageable
    ) {
        Page<SnapshotPolicyResponse> response = policyServicePort.getPolicies(pageable, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> getPolicyDetails(
            String token,
            Long policyId
    ) {
        SnapshotPolicyResponse response = policyServicePort.getPolicyDetails(policyId, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> createPolicy(
            String token,
            SnapshotPolicyRequest request
    ) {
        SnapshotPolicyResponse response = policyServicePort.createPolicy(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<SnapshotPolicyResponse> updatePolicy(
            String token,
            Long policyId,
            SnapshotPolicyRequest request
    ) {
        SnapshotPolicyResponse response = policyServicePort.updatePolicy(policyId, request, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deletePolicy(
            String token,
            Long policyId
    ) {
        policyServicePort.deletePolicy(policyId, token);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deactivatePolicy(
            String token,
            Long policyId
    ) {
        policyServicePort.deactivatePolicy(policyId, token);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> activatePolicy(
            String token,
            Long policyId
    ) {
        policyServicePort.activatePolicy(policyId, token);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page<SnapshotTaskResponse>> getPolicyRuns(
            String token,
            Long policyId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            Pageable pageable
    ) {
        Page<SnapshotTaskResponse> response = policyServicePort.getPolicyRuns(policyId, since, pageable, token);
        return ResponseEntity.ok(response);
    }
}

