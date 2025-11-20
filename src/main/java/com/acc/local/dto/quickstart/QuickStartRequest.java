package com.acc.local.dto.quickstart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuickStartRequest {

    @Schema(description = "생성할 인스턴스 이름", example = "my-instance")
    private String instanceName;

    @Schema(description = "선택한 type(flavor) ID", example = "fef0df7f-c3bd-4c36-941c-f476e9f155f3")
    private String typeId;

    @Schema(description = "생성할 볼륨 크기 (GB)", example = "20")
    private Integer volumeSize;

    @Schema(description = "생성할 인스턴스의 관리자 계정 비밀번호", example = "1234")
    private String password;

    @Schema(description = "외부 네트워크 연결 여부", example = "true")
    private Boolean isExternal;

}
