package com.acc.local.service.ports;

import com.acc.local.dto.quickcreate.QuickSettingFormResponse;
import com.acc.local.dto.quickcreate.QuickSettingRequest;

public interface QuickSettingServicePort {

    public QuickSettingFormResponse getForm(String token, String projectId);
    public void create(String token, String projectId, QuickSettingRequest request);
}
