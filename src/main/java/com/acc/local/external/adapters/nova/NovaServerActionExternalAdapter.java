package com.acc.local.external.adapters.nova;

import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.external.dto.nova.serverAction.*;
import com.acc.local.external.modules.nova.NovaServerActionAPIModule;
import com.acc.local.external.ports.NovaServerActionExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@RequiredArgsConstructor
public class NovaServerActionExternalAdapter implements NovaServerActionExternalPort {

    private final NovaServerActionAPIModule novaServerActionAPIModule;

    @Override
    public void callControlInstance(String token, String projectId, String instanceId, InstanceActionRequest request) {
        ResponseEntity<JsonNode> response;

        try {
            switch (request.getAction()) {
                case START:
                    response = novaServerActionAPIModule.startServer(token, instanceId, new StartServerRequest());
                    break;
                case STOP:
                    response = novaServerActionAPIModule.stopServer(token, instanceId, new StopServerRequest());
                    break;
                case PAUSE:
                    response = novaServerActionAPIModule.pauseServer(token, instanceId, new PauseServerRequest());
                    break;
                case UNPAUSE:
                    response = novaServerActionAPIModule.unpauseServer(token, instanceId, new UnpauseServerRequest());
                    break;
                case SUSPEND:
                    response = novaServerActionAPIModule.suspendServer(token, instanceId, new SuspendServerRequest());
                    break;
                case RESUME:
                    response = novaServerActionAPIModule.resumeServer(token, instanceId, new ResumeServerRequest());
                    break;
                case UNLOCK:
                    response = novaServerActionAPIModule.unlockServer(token, instanceId, new UnlockServerRequest());
                    break;
                case SHELVE:
                    response = novaServerActionAPIModule.shelveServer(token, instanceId, new ShelveServerRequest());
                    break;
                case SHELVE_OFFLOAD:
                    response = novaServerActionAPIModule.shelveOffloadServer(token, instanceId, new ShelveOffloadServerRequest());
                    break;
                case FORCE_DELETE:
                    response = novaServerActionAPIModule.forceDeleteServer(token, instanceId, new ForceDeleteServerRequest());
                    break;
                case RESTORE:
                    response = novaServerActionAPIModule.restoreServer(token, instanceId, new RestoreServerRequest());
                    break;
                case REVERT_RESIZE:
                    response = novaServerActionAPIModule.revertResize(token, instanceId, new RevertResizeRequest());
                    break;
                case UNRESCUE:
                    response = novaServerActionAPIModule.unrescueServer(token, instanceId, new UnrescueServerRequest());
                    break;
                case CONFIRM_RESIZE:
                    response = novaServerActionAPIModule.confirmResizeServer(token, instanceId, new ConfirmResizeRequest());
                    break;

                case LOCK:
                    LockServerRequest.LockInfo lockInfo = null;
                    if (request.getLockedReason() != null && !request.getLockedReason().isBlank()) {
                        lockInfo = LockServerRequest.LockInfo.builder()
                                .lockedReason(request.getLockedReason())
                                .build();
                    }
                    response = novaServerActionAPIModule.lockServer(token, instanceId, new LockServerRequest(lockInfo));
                    break;

                case REBOOT:
                    String rebootType = (request.getRebootType() != null) ? request.getRebootType() : "SOFT";
                    response = novaServerActionAPIModule.rebootServer(token, instanceId, rebootType);
                    break;

                case ADD_SECURITY_GROUP:
                    response = novaServerActionAPIModule.addSecurityGroupToServer(token, instanceId, request.getSecurityGroupName());
                    break;

                case REMOVE_SECURITY_GROUP:
                    response = novaServerActionAPIModule.removeSecurityGroupFromServer(token, instanceId, request.getSecurityGroupName());
                    break;

                case CHANGE_PASSWORD:
                    response = novaServerActionAPIModule.changeAdminPassword(token, instanceId, request.getAdminPassword());
                    break;

                case CREATE_BACKUP:
                    CreateBackupRequest.BackupInfo backupInfo = CreateBackupRequest.BackupInfo.builder()
                            .name(request.getBackupName())
                            .backupType(request.getBackupType())
                            .rotation(request.getRotation())
                            .metadata(request.getMetadata())
                            .build();
                    response = novaServerActionAPIModule.createServerBackup(token, instanceId, new CreateBackupRequest(backupInfo));
                    break;

                case CREATE_IMAGE:
                    CreateImageRequest.ImageInfo imageInfo = CreateImageRequest.ImageInfo.builder()
                            .name(request.getImageName())
                            .metadata(request.getMetadata())
                            .build();
                    response = novaServerActionAPIModule.createImage(token, instanceId, new CreateImageRequest(imageInfo));
                    break;

                case RESIZE:
                    ResizeServerRequest.ResizeInfo resizeInfo = ResizeServerRequest.ResizeInfo.builder()
                            .flavorRef(request.getFlavorRef())
                            .diskConfig(request.getDiskConfig())
                            .build();
                    response = novaServerActionAPIModule.resizeServer(token, instanceId, new ResizeServerRequest(resizeInfo));
                    break;

                case REBUILD:
                    RebuildServerRequest.RebuildInfo rebuildInfo = RebuildServerRequest.RebuildInfo.builder()
                            .imageRef(request.getImageRef())
                            .name(request.getImageName())
                            .adminPass(request.getAdminPassword())
                            .metadata(request.getMetadata())
                            .diskConfig(request.getDiskConfig())
                            .build();
                    response = novaServerActionAPIModule.rebuildServer(token, instanceId, new RebuildServerRequest(rebuildInfo));
                    break;

                case RESCUE:
                    RescueServerRequest.RescueInfo rescueInfo = RescueServerRequest.RescueInfo.builder()
                            .adminPass(request.getRescueAdminPass())
                            .rescueImageRef(request.getRescueImageRef())
                            .build();
                    response = novaServerActionAPIModule.rescueServer(token, instanceId, new RescueServerRequest(rescueInfo));
                    break;

                case UNSHELVE:
                    UnshelveServerRequest.UnshelveInfo unshelveInfo = null;
                    if (request.getAvailabilityZone() != null || request.getHost() != null) {
                        unshelveInfo = UnshelveServerRequest.UnshelveInfo.builder()
                                .availabilityZone(request.getAvailabilityZone())
                                .host(request.getHost())
                                .build();
                    }
                    response = novaServerActionAPIModule.unshelveServer(token, instanceId, new UnshelveServerRequest(unshelveInfo));
                    break;

                default:
                    String customErrorMessage = "지원되지 않는 Action: " + request.getAction();
                    throw new NovaException(NovaErrorCode.NOVA_UNSUPPORTED_ACTION, customErrorMessage);
            }
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_ACTION_FAILED);
        }

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_ACTION_FAILED);
        }
    }
}
