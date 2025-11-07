package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.InstanceDocs;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.service.ports.InstanceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InstanceController implements InstanceDocs {

    private final InstanceServicePort instanceServicePort;

    @Override
    public ResponseEntity<PageResponse<InstanceResponse>> getInstances(String token, PageRequest page) {
        PageResponse<InstanceResponse> response = instanceServicePort.getInstances(page, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> createInstance(String token, @RequestBody InstanceCreateRequest request) {
        instanceServicePort.createInstance(request, token);
        return ResponseEntity.created(null).build();
    }

    @Override
    public ResponseEntity<Object> controlInstance(String token, String instanceId, @RequestBody InstanceActionRequest request) {
        instanceServicePort.controlInstance(instanceId, request, token);
        return ResponseEntity.ok().build();
    }
}

