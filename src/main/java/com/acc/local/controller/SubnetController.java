package com.acc.controller;

import com.acc.dto.subnet.*;
import com.acc.local.service.ports.SubnetPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subnets")
public class SubnetController {

    private final SubnetPort subnetPort;

    @PostMapping
    public ResponseEntity<SubnetResponse> create(@RequestHeader("X-Auth-Token") String token,
                                                 @Valid @RequestBody SubnetRequest request) {
        return ResponseEntity.ok(subnetPort.createSubnet(request, token));
    }

    @GetMapping
    public ResponseEntity<List<SubnetResponse>> list(@RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(subnetPort.listSubnets(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubnetResponse> update(@PathVariable String id,
                                                 @RequestHeader("X-Auth-Token") String token,
                                                 @Valid @RequestBody SubnetUpdateRequest request) {
        return ResponseEntity.ok(subnetPort.updateSubnet(id, request, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @RequestHeader("X-Auth-Token") String token) {
        subnetPort.deleteSubnet(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<SubnetResponse>> search(@RequestParam String keyword,
                                                       @RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(subnetPort.searchSubnets(keyword, token));
    }
}
