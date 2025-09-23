package com.acc.local.service.ports;

import com.acc.local.dto.flavor.FlavorDto;

import java.util.List;

public interface FlavorServicePort {
    List<FlavorDto> getAllFlavors(String token);
    FlavorDto getFlavorById(String token, String id);
}
