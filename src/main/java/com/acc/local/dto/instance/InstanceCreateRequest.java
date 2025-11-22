package com.acc.local.dto.instance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceCreateRequest {

    @Schema(description = "인스턴스 이름", example = "my-vm-server")
    String instanceName;

    @Schema(description = "인스턴스 타입(Flavor) ID", example = "flavor-uuid-1234")
    String typeId;

    @Schema(description = "인스턴스 이미지 ID", example = "image-uuid-5678")
    String imageId;

    @Schema(description = "연결할 네트워크 ID 목록", example = "[\"network-uuid-1\", \"network-uuid-2\"]")
    List<String> networkIds;

    @Schema(description = "연결할 인터페이스(Port) ID 목록", example = "[\"port-uuid-1\"]")
    List<String> interfaceIds;

    @Schema(description = "적용할 보안 그룹 ID 목록", example = "[\"sg-uuid-default\"]")
    List<String> securityGroupIds;

    @Schema(description = "부트 볼륨 크기 (GB). 0 또는 null이면 이미지 기본 크기 사용", example = "50")
    Integer diskSize;

    @Schema(description = "관리자 비밀번호 (keypairId와 택1)", example = "Password!@#123")
    String password;

    @Schema(description = "키페어 이름 (password와 택1)", example = "my-keypair-name")
    String keypairId;
}

