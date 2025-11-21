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
