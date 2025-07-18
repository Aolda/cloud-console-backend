package com.acc.controller;

import com.acc.dto.port.*;
import com.acc.local.service.ports.PortPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ports")
public class PortController {

    private final PortPort portPort;

    @PostMapping
    public ResponseEntity<PortResponse> createPort(@RequestBody PortRequest request, @RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(portPort.createPort(request, token));
    }

    @GetMapping
    public ResponseEntity<List<PortResponse>> listPorts(@RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(portPort.listPorts(token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortResponse> updatePort(@PathVariable String id, @RequestBody PortUpdateRequest request, @RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(portPort.updatePort(id, request, token));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePort(@PathVariable String id, @RequestHeader("X-Auth-Token") String token) {
        portPort.deletePort(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PortResponse>> searchPort(@RequestParam String keyword, @RequestHeader("X-Auth-Token") String token) {
        return ResponseEntity.ok(portPort.searchPorts(keyword, token));
    }
}