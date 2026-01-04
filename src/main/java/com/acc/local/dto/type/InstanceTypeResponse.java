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

    @Schema(description = "인스턴스 타입 ID (UUID)", example = "09085288-78f3-4c40-93db-dc1d4d9f81be")
    private String typeId;

    @Schema(description = "인스턴스 타입 이름", example = "tb1.medium")
    private String typeName;

    @Schema(description = "아키텍처 타입 (X86, HETEROGENEOUS 등)", example = "X86")
    private Architecture architect;

    @Schema(description = "인스턴스 유형/목적 (GENERAL, COMPUTE, MEMORY, HIGH_CLOCK)", example = "GENERAL")
    private Purpose purpose;

    @Schema(description = "vCPU 코어 수 (단위: 개)", example = "2")
    private Integer core;

    @Schema(description = "메모리 크기 (단위: MiB)", example = "4096")
    private Integer ram;

    @Schema(description = "루트 디스크 크기 (단위: GiB)", example = "40")
    private Integer diskSize;

    @Schema(description = "내부 네트워크 대역폭 (단위: Gbps)", example = "10")
    private Integer bandwidth;

    @Schema(description = "스토리지 IOPS (단위: IOPS)", example = "3000")
    private Integer iops;

    @Schema(description = "NUMA 노드 수 (단위: 개)", example = "1")
    private Integer numa;

    @Schema(description = "USB 장치 허용 여부", example = "false")
    private Boolean usb;

    @JsonProperty("public")
    @Schema(description = "공개 여부 (Public: 모든 사용자, Private: 특정 프로젝트만)", example = "true")
    private Boolean isPublic;

    @Schema(description = "타입 설명", example = "범용 미디엄 인스턴스 - 일반적인 워크로드에 적합")
    private String description;
}
