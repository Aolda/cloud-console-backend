package com.acc.local.service.modules.volume;

import org.springframework.stereotype.Component;

@Component
public class VolumeUtil {

    private static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private static final String VOLUME_NAME_REGEX =
            "^[a-zA-Z\u4e00-\u9fa5][0-9a-zA-Z_\\[\\]().:^-]{0,127}$";

    public boolean validateVolumeId(String volumeId) {
        if (volumeId == null || volumeId.isEmpty()) {
            return false;
        }
        return volumeId.matches(UUID_REGEX);
    }

    public boolean validateVolumeName(String name) {
        if (name == null || name.isEmpty()) {
            return true; // 이름은 선택사항
        }
        return name.matches(VOLUME_NAME_REGEX);
    }

    public boolean validateVolumeSize(Integer size) {
        if (size == null) {
            return false;
        }
        return size > 0;
    }
}