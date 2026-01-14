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

    @Schema(description = "인스턴스 이름 (Ubuntu 호스트명 규칙). 알파벳/숫자로 시작, 알파벳/숫자/하이픈(-) 사용 가능, 하이픈으로 끝날 수 없음, 최대 63자", requiredMode = Schema.RequiredMode.REQUIRED, example = "my-vm-server")
    String instanceName;

    @Schema(description = "인스턴스 타입(Flavor) ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "flavor-uuid-1234")
    String typeId;

    @Schema(description = "인스턴스 이미지 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "image-uuid-5678")
    String imageId;

    @Schema(description = "연결할 네트워크 UUID 목록. (interfaceIds와 동시 사용 가능, 둘 중 최소 1개 필요)", example = "[\"network-uuid-1\", \"network-uuid-2\"]")
    List<String> networkIds;

    @Schema(description = "연결할 기존 포트 UUID 목록. 이미 생성된 포트를 사용합니다. (networkIds와 동시 사용 가능, 둘 중 최소 1개 필요)", example = "[\"port-uuid-1\"]")
    List<String> interfaceIds;

    @Schema(description = "적용할 보안 그룹 ID 목록 (선택)", example = "[\"sg-uuid-default\", \"sg-uuid-web\"]")
    List<String> securityGroupIds;

    @Schema(description = "부트 볼륨 크기 (GB). null 또는 0이면 이미지 기본 크기 사용 (선택)", example = "50")
    Integer diskSize;

    @Schema(description = "관리자 비밀번호 (keypairName과 택1. 둘 중 하나는 필수)", example = "Password!@#123")
    String password;

    @Schema(description = "키페어 이름 (password와 택1. 둘 중 하나는 필수). OpenStack에 등록된 키페어 이름을 사용해야 합니다. 새 키페어가 필요한 경우 키페어 생성 API를 먼저 사용하세요.", example = "my-keypair")
    String keypairName;
}

