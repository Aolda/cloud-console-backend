package com.acc.local.controller;

import com.acc.local.dto.compute.ComputeDetailResponse;
import com.acc.local.dto.compute.ComputeResponse;
import com.acc.local.service.ports.ComputeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/computes")
public class ComputeController {
    private final ComputeServicePort computeServicePort;

    @GetMapping
    public List<ComputeResponse> getComputes(@RequestHeader("X-Auth-Token") String token) {
        return computeServicePort.getComputes(token);
    }

    @GetMapping("/detail")
    public List<ComputeDetailResponse> getComputeDetail(@RequestHeader("X-Auth-Token") String token) {
        return computeServicePort.getComputeDetail(token);
    }

}
