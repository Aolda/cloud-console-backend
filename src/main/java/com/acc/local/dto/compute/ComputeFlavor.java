package com.acc.local.dto.compute;

import java.util.List;

public record ComputeFlavor(
        String id,
        List<ComputeLink> links
) {}
