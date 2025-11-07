package com.acc.local.service.modules.instance;

import com.acc.global.exception.instance.InstanceErrorCode;
import com.acc.global.exception.instance.InstanceException;
import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.local.domain.enums.InstanceStatus;
import com.acc.local.dto.instance.InstanceActionRequest;
import org.springframework.stereotype.Component;

@Component
public class InstanceUtil {

    public boolean validateInstanceName(String instanceName) {
        return instanceName != null && !instanceName.isEmpty() &&
                instanceName.matches("^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$");
    }

    public boolean validateAuthMethod(String keypairId, String password) {
        boolean hasKey = (keypairId != null && !keypairId.isEmpty());
        boolean hasPass = (password != null && !password.isEmpty());

        return hasKey ^ hasPass; // 인증 방식은 '키페어' 또는 '패스워드' 중 하나 (XOR)
    }

    public boolean validateActionWithStatus(InstanceStatus rawStatus, String action) {

        return switch (action) {
            // [ 'ACTIVE' 상태로 만드는 동작 ]
            case "start" -> rawStatus == InstanceStatus.SHUTOFF;
            case "unpause" -> rawStatus == InstanceStatus.PAUSED;
            case "unshelve" -> rawStatus == InstanceStatus.SHELVED || rawStatus == InstanceStatus.SHELVED_OFFLOADED;
            case "resume" -> rawStatus == InstanceStatus.SUSPENDED;

            // [ 'ACTIVE' 상태에서 수행하는 동작 ]
            case "stop" -> rawStatus == InstanceStatus.ACTIVE;
            case "pause" -> rawStatus == InstanceStatus.ACTIVE;
            case "suspend" -> rawStatus == InstanceStatus.ACTIVE;
            case "reboot" -> rawStatus == InstanceStatus.ACTIVE || rawStatus == InstanceStatus.ERROR;
            case "hard_reboot" ->
                    rawStatus == InstanceStatus.ACTIVE || rawStatus == InstanceStatus.SHUTOFF || rawStatus == InstanceStatus.ERROR;

            // [ 상태 변경 동작 ]
            case "shelve" -> rawStatus == InstanceStatus.ACTIVE || rawStatus == InstanceStatus.SHUTOFF;
            case "rebuild", "rescue", "changePassword" ->
                    rawStatus == InstanceStatus.ACTIVE || rawStatus == InstanceStatus.SHUTOFF;
            case "resize" -> rawStatus == InstanceStatus.ACTIVE || rawStatus == InstanceStatus.SHUTOFF;
            case "confirmResize" -> rawStatus == InstanceStatus.VERIFY_RESIZE;
            case "revertResize" -> rawStatus == InstanceStatus.VERIFY_RESIZE;

            // [ 삭제 동작 ]
            case "delete" -> rawStatus != InstanceStatus.DELETED && rawStatus != InstanceStatus.REVERT_RESIZE;
            default -> false;
        };
    }

    public void validateInstanceActionRequest(InstanceActionRequest request) {
        switch (request.getAction()) {
            case REMOVE_SECURITY_GROUP:
                validateParameter(request.getSecurityGroupName(), "securityGroupName");
                break;

            case CHANGE_PASSWORD:
                validateParameter(request.getAdminPassword(), "adminPassword");
                break;

            case CREATE_BACKUP:
                validateParameter(request.getBackupName(), "backupName");
                validateParameter(request.getBackupType(), "backupType");
                validateParameter(request.getRotation(), "rotation");
                break;

            case CREATE_IMAGE:
                validateParameter(request.getImageName(), "imageName");
                break;

            case RESIZE:
                validateParameter(request.getFlavorRef(), "flavorRef");
                break;

            case REBUILD:
                validateParameter(request.getImageRef(), "imageRef");
                break;

            default:
                break;
        }
    }

    private void validateParameter(Object param, String paramName) {
        if (param == null) {
            String customErrorMessage = "필수 파라미터가 누락: " + paramName;
            throw new InstanceException(InstanceErrorCode.INVALID_PARAMETER, customErrorMessage);
        }
        if (param instanceof String && ((String) param).isBlank()) {
            String customErrorMessage = "파라미터는 공백일 수 없습니다: " + paramName;
            throw new InstanceException(InstanceErrorCode.INVALID_PARAMETER, customErrorMessage);
        }
    }
}
