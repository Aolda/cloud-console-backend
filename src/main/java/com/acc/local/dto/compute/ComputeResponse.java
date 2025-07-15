package com.acc.local.dto.compute;

import java.util.List;

public record ComputeResponse (
    String id,
    String name,
    List<ComputeLink> computeLinks
){}
