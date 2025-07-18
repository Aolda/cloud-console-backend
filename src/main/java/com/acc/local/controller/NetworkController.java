package com.acc.controller;

import com.acc.dto.network.*;
import com.acc.local.service.ports.NetworkPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/networks")
public class NetworkController {

    private final NetworkPort networkPort;

    @PostMapping
    public ResponseEntity<NetworkResponse> create(@RequestHeader("X-Auth-Token") String token,
                                                  @Valid @RequestBody NetworkRequest request) {
        return ResponseEntity.ok(networkPort.createNetwork(request, token));
    }

    @GetMapping
    public ResponseEntity<List<NetworkResponse>> list(@RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(networkPort.listNetworks(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NetworkResponse> update(@PathVariable String id,
                                                  @RequestHeader("X-Auth-Token") String token,
                                                  @Valid @RequestBody NetworkUpdateRequest request) {
        return ResponseEntity.ok(networkPort.updateNetwork(id, request, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @RequestHeader("X-Auth-Token") String token) {
        networkPort.deleteNetwork(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<NetworkResponse>> searchByName(@RequestParam String keyword,
                                                              @RequestHeader("X-Auth-Token") String token) {
        List<NetworkResponse> all = networkPort.listNetworks(token);
        List<NetworkResponse> filtered = all.stream()
                .filter(n -> n.name() != null && n.name().contains(keyword))
                .toList();
        return ResponseEntity.ok(filtered);
    }
}
