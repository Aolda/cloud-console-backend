package com.acc.local.service.adapters.compute;

import com.acc.local.dto.compute.ComputeDetailResponse;
import com.acc.local.dto.compute.ComputeResponse;
import com.acc.local.service.modules.compute.ComputeModule;
import com.acc.local.service.ports.ComputePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class ComputeAdapter implements ComputePort {
    private final ComputeModule computeModule;
    @Override
    public List<ComputeResponse> getComputes(String token){
        return computeModule.getComputes(token);
    }
    @Override
    public List<ComputeDetailResponse> getComputeDetail(String token ) {
        return computeModule.getComputeDetail(token);
    }

}
