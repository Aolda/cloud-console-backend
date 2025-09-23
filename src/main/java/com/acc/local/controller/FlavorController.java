package com.acc.local.controller;

import com.acc.local.dto.flavor.FlavorDto;
import com.acc.local.service.ports.FlavorServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/flavors")
public class FlavorController {

    private final FlavorServicePort flavorServicePort;

    @GetMapping
    public List<FlavorDto> getAllFlavors(@RequestHeader("X-Auth-Token") String token) {
        return flavorServicePort.getAllFlavors(token);
    }

    @GetMapping("/{id}")
    public FlavorDto getFlavorById(@RequestHeader("X-Auth-Token") String token, @PathVariable String id) {
        return flavorServicePort.getFlavorById(token, id);
    }
}
