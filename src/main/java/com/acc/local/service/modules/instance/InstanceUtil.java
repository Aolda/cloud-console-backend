package com.acc.local.service.modules.instance;

import com.acc.global.exception.instance.InstanceErrorCode;
import com.acc.global.exception.instance.InstanceException;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.project.quota.ProjectComputeQuotaDto;
import org.springframework.stereotype.Component;

@Component
public class InstanceUtil {

    /**
     * 인스턴스 이름 검증 (RFC 1123 호스트명 규칙)
     * - 알파벳이나 숫자로 시작
     * - 중간에는 알파벳, 숫자, 하이픈(-) 허용
     * - 하이픈으로 끝날 수 없음
     * - 최대 63자
     */
    public boolean validateInstanceName(String instanceName) {
        return instanceName != null && !instanceName.isEmpty() &&
                instanceName.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$");
    }

    public boolean validateAuthMethod(String keypairId, String password) {
        boolean hasKey = (keypairId != null && !keypairId.isEmpty());
        boolean hasPass = (password != null && !password.isEmpty());

        return hasKey ^ hasPass; // 인증 방식은 '키페어' 또는 '패스워드' 중 하나 (XOR)
    }

    public boolean validateNetworkConnection(java.util.List<String> networkIds, java.util.List<String> interfaceIds) {
        boolean hasNetwork = (networkIds != null && !networkIds.isEmpty());
        boolean hasInterface = (interfaceIds != null && !interfaceIds.isEmpty());

        return hasNetwork || hasInterface; // 네트워크 또는 인터페이스 중 최소 1개 필요 (OR)
    }

    // vCPU, RAM 쿼터는 Flavor 정보를 조회해야 정확히 검증 가능합니다.
    // 현재는 인스턴스 개수만 검증하며, 나머지는 OpenStack Nova API에서 검증합니다.
    public void validateQuotaForInstanceCreation(ProjectComputeQuotaDto quota) {
        // 인스턴스 개수 쿼터 검증
        if (quota.instance().available() < 1) {
            throw new InstanceException(InstanceErrorCode.COMPUTE_QUOTA_EXCEEDED);
        }
    }

    public void validateInstanceActionRequest(InstanceActionRequest request) {
        switch (request.getAction()) {
            case REMOVE_SECURITY_GROUP:
                validateParameter(request.getSecurityGroupName(), "securityGroupName");
                break;

            case CHANGE_PASSWORD:
                validateParameter(request.getAdminPassword(), "adminPassword");
                break;


            case RESIZE:
                validateParameter(request.getFlavorRef(), "flavorRef");
                break;

//            case CREATE_BACKUP:
//                validateParameter(request.getBackupName(), "backupName");
//                validateParameter(request.getBackupType(), "backupType");
//                validateParameter(request.getRotation(), "rotation");
//                break;
//
//            case CREATE_IMAGE:
//                validateParameter(request.getImageName(), "imageName");
//                break;

//            case REBUILD:
//                validateParameter(request.getImageRef(), "imageRef");
//                break;

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
