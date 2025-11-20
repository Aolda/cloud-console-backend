package com.acc.local.service.ports;

import com.acc.local.dto.quickstart.QuickStartRequest;
import com.acc.local.dto.quickstart.QuickStartResponse;

public interface QuickStartServicePort {

    QuickStartResponse create(String userId, String projectId, QuickStartRequest request);
}
