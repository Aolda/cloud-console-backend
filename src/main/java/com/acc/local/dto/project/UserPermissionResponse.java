package com.acc.local.dto.project;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserPermissionResponse (

    @Schema(description = "키스톤 프로젝트 Id")
    String projectId,

    @Schema(description = "키스톤 프로젝트에 대한 사용자 권한 : admin, manage, guest, null(권한 없음)")
    String projectPermission
) {}
