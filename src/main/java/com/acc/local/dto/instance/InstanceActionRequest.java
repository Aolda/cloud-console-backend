package com.acc.local.dto.instance;

import com.acc.local.domain.enums.InstanceActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;

@Getter
@NoArgsConstructor
public class InstanceActionRequest {

    @Schema(description = "수행할 작업 Enum", example = "REBOOT")
    InstanceActionType action;

    @Schema(description = "[LOCK] 잠금 사유 (v2.73+)", example = "작업 중")
    String lockedReason;

    @Schema(description = "[REBOOT] 재부팅 유형", example = "SOFT")
    String rebootType; // "SOFT" or "HARD"

    @Schema(description = "[ADD/REMOVE_SECURITY_GROUP] 보안 그룹 이름", example = "sg-default")
    String securityGroupName;

    @Schema(description = "[CHANGE_PASSWORD] 새 관리자 비밀번호", example = "NewP@ssw0rd!")
    String adminPassword;

    @Schema(description = "[RESIZE] 변경할 인스턴스 타입(Flavor) ID", example = "flavor-uuid-new")
    String flavorRef;

    @Schema(description = "[RESIZE] 디스크 설정 (AUTO 또는 MANUAL)", example = "AUTO")
    String diskConfig;

    @Schema(description = "[CREATE_IMAGE / REBUILD] 이미지/서버 이름", example = "my-snapshot-image")
    String imageName; // CREATE_IMAGE의 name, REBUILD의 name

    @Schema(description = "[REBUILD] 리빌드할 이미지 ID", example = "image-uuid-new")
    String imageRef;

    @Schema(description = "[CREATE_IMAGE / CREATE_BACKUP / REBUILD] 메타데이터")
    Map<String, String> metadata;

    @Schema(description = "[CREATE_BACKUP] 생성할 백업 이름", example = "my-daily-backup")
    String backupName;

    @Schema(description = "[CREATE_BACKUP] 백업 유형", example = "daily")
    String backupType;

    @Schema(description = "[CREATE_BACKUP] 로테이션 횟수", example = "7")
    Integer rotation;

    @Schema(description = "[RESCUE] 복구 모드용 비밀번호", example = "RescueP@ss!")
    String rescueAdminPass;

    @Schema(description = "[RESCUE] 복구 모드용 이미지 ID", example = "rescue-image-uuid")
    String rescueImageRef;

    @Schema(description = "[UNSHELVE] 보관 해제 시 배치할 가용 영역", example = "az-west")
    String availabilityZone;

    @Schema(description = "[UNSHELVE] 보관 해제 시 배치할 호스트", example = "compute-node-01")
    String host;
}
