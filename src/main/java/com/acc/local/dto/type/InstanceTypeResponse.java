package com.acc.local.dto.type;

import com.acc.local.domain.enums.Architecture;
import com.acc.local.domain.enums.Purpose;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceTypeResponse {

    @Schema(description = "인스턴스 타입 ID (UUID)", example = "flavor-uuid-1234")
    private String typeId;

    @Schema(description = "인스턴스 타입 이름", example = "tb1.micro")
    private String typeName;

    @Schema(description = "아키텍처 타입 (Enum)", example = "X86")
    private Architecture architect;

    @Schema(description = "인스턴스 유형/목적 (Enum)", example = "GENERAL")
    private Purpose purpose;

    @Schema(description = "vCPU 코어 수", example = "1")
    private Integer core;

    @Schema(description = "메모리 크기 (MiB)", example = "1024")
    private Integer ram;

    @Schema(description = "루트 디스크 크기 (GiB)", example = "20")
    private Integer diskSize;

    @Schema(description = "내부 네트워크 대역폭 (Gbps)", example = "10")
    private Integer bandwidth;

    @Schema(description = "스토리지 IOPS", example = "1000")
    private Integer iops;

    @Schema(description = "NUMA 노드 수", example = "1")
    private Integer numa;

    @Schema(description = "USB 장치 허용 여부", example = "false")
    private Boolean usb;

    @JsonProperty("public")
    @Schema(description = "공개 여부", example = "true")
    private Boolean isPublic;

    @Schema(description = "설명", example = "범용 마이크로 인스턴스")
    private String description;
}
