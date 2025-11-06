package com.acc.local.service.modules.volume.snapshot;

import org.springframework.stereotype.Component;

@Component
public class VolumeSnapshotUtil {

    private static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private static final String SNAPSHOT_NAME_REGEX =
            "^[a-zA-Z\u4e00-\u9fa5][0-9a-zA-Z_\\[\\]().:^-]{0,127}$";


    public boolean validateSnapshotId(String snapshotId) {
        if (snapshotId == null || snapshotId.isEmpty()) {
            return false;
        }
        return snapshotId.matches(UUID_REGEX);
    }

    public boolean validateVolumeId(String volumeId) {
        if (volumeId == null || volumeId.isEmpty()) {
            return false;
        }
        return volumeId.matches(UUID_REGEX);
    }

    public boolean validateSnapshotName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.matches(SNAPSHOT_NAME_REGEX);
    }
}
