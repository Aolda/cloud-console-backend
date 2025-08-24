package com.acc.local.dto.auth;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserPermissionResponse {


    @Schema(description = "키스톤 프로젝트 Id")
    private String projectId;

    @Schema(description = "키스톤 프로젝트에 대한 사용자 권한 : admin, manage, guest, null(권한 없음)")
    private String projectPermission;
}
