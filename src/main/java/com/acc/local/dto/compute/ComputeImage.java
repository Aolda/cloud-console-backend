package com.acc.local.dto.compute;

import java.util.List;

public record ComputeImage(
        String id,
        List<ComputeLink> links
) {}
