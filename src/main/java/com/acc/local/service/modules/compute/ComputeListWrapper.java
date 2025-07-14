package com.acc.local.service.modules.compute;
import java.util.List;

public record ComputeListWrapper<T>(
        List<T> servers
) {}
