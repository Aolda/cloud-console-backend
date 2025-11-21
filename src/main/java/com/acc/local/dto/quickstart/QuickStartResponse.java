package com.acc.local.dto.quickstart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickStartResponse {

    @Schema(description = "접속 가능한 물리노드 포트", example = "20000")
    private String serverPort;
}
