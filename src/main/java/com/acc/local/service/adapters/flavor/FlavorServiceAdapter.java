package com.acc.local.service.adapters.flavor;

import com.acc.local.dto.flavor.FlavorDto;
import com.acc.local.service.modules.flavor.FlavorModule;
import com.acc.local.service.ports.FlavorServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FlavorServiceAdapter implements FlavorServicePort {

    private final FlavorModule flavorModule;

    @Override
    public List<FlavorDto> getAllFlavors(String token) {
        return flavorModule.getAllFlavors(token);
    }

    @Override
    public FlavorDto getFlavorById(String token, String id) {
        return flavorModule.getFlavorById(token, id);
    }
}
