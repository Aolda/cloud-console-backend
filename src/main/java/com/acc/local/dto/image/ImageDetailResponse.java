package com.acc.local.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "이미지 상세 정보")
public record ImageDetailResponse(
        @Schema(description = "이미지 ID")
        String id,
        @Schema(description = "이미지 이름")
        String name,
        @Schema(description = "아키텍처")
        String architecture,
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "이미지 설명")
        String description,
        @Schema(description = "디스크 포맷")
        String diskFormat,
        @Schema(description = "컨테이너 포맷")
        String containerFormat,
        @Schema(description = "가시성 (public/private)")
        String visibility,
        @Schema(description = "이미지 상태")
        String status,
        @Schema(description = "이미지 크기(바이트)")
        Long size,
        @Schema(description = "가상 디스크 크기(바이트)")
        Long virtualSize,
        @Schema(description = "최소 디스크 크기(GiB)")
        Integer minDisk,
        @Schema(description = "최소 메모리 크기(MiB)")
        Integer minRam,
        @Schema(description = "체크섬")
        String checksum,
        @Schema(description = "해시 알고리즘")
        String osHashAlgo,
        @Schema(description = "해시 값")
        String osHashValue,
        @Schema(description = "OS 버전")
        String osVersion,
        @Schema(description = "OS 관리자 사용자명")
        String osAdminUser,
        @Schema(description = "삭제 보호 여부")
        Boolean deleteProtected,
        @Schema(description = "숨김 여부")
        Boolean hidden,
        @Schema(description = "태그 목록")
        List<String> tags,
        @Schema(description = "저장소")
        String stores,
        @Schema(description = "생성 시간")
        String createdAt,
        @Schema(description = "수정 시간")
        String updatedAt
) {}
