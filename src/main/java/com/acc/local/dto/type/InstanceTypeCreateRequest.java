package com.acc.local.dto.type;

import com.acc.local.domain.enums.Architecture;
import com.acc.local.domain.enums.Purpose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceTypeCreateRequest {

    @Schema(description = "인스턴스 타입(Flavor) 이름", requiredMode = Schema.RequiredMode.REQUIRED, example = "tb1.micro")
    private String typeName;

    @Schema(description = "아키텍처 타입 (X86 또는 HETEROGENEOUS)", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Architecture.class, example = "X86")
    private Architecture architect;

    @Schema(description = "인스턴스 유형/목적 (GENERAL, COMPUTE, MEMORY 등)", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Purpose.class, example = "GENERAL")
    private Purpose purpose;

    @Schema(description = "vCPU 코어 수 (단위: 개)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer core;

    @Schema(description = "메모리 크기 (단위: MiB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Integer ram;

    @Schema(description = "루트 디스크 크기 (단위: GiB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer diskSize;

    @Schema(description = "내부 네트워크 대역폭 (단위: Gbps, 선택)", example = "10")
    private Integer bandwidth;

    @Schema(description = "NUMA 노드 수 (선택)", example = "1")
    private Integer numa;

    @Schema(description = "USB 장치 허용 여부 (선택)", example = "false")
    private Boolean usb;
}
