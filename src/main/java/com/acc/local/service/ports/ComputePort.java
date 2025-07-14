package com.acc.local.service.ports;

import com.acc.local.dto.compute.ComputeDetailResponse;
import com.acc.local.dto.compute.ComputeResponse;

import java.util.List;

public interface ComputePort {
    List<ComputeResponse> getComputes(String token);
    List<ComputeDetailResponse> getComputeDetail(String token);

}
