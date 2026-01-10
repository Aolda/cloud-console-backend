package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.SubnetDocs;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;
import com.acc.local.service.ports.SubnetServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SubnetController implements SubnetDocs {

    private final SubnetServicePort subnetServicePort;

    @Override
    public ResponseEntity<PageResponse<ViewSubnetsResponse>> viewSubnets(Authentication authentication, PageRequest page, String networkId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(
                subnetServicePort.listSubnets(
                        page,
                        networkId,
                        projectId,
                        jwtInfo.getUserId()
                )
        );
    }

    @Override
    public ResponseEntity<ViewSubnetsResponse> getSubnet(Authentication authentication, String subnetId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return ResponseEntity.ok(
                subnetServicePort.getSubnetDetail(
                        subnetId,
                        projectId,
                        jwtInfo.getUserId()
                )
        );
    }

    @Override
    public ResponseEntity<Object> createSubnet(Authentication authentication, CreateSubnetRequest request, String networkId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        subnetServicePort.createSubnet(request, networkId, projectId, jwtInfo.getUserId());
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> deleteSubnet(Authentication authentication, String subnetId, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        subnetServicePort.deleteSubnet(subnetId, projectId, jwtInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
