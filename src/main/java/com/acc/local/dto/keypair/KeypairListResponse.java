package com.acc.local.dto.keypair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeypairListResponse {

    @Schema(description = "키페어 ID (핑거프린트)", example = "aa:bb:cc:dd:ee:ff:11:22:33:44:55:66:77:88:99:00")
    private String keypairId;

    @Schema(description = "키페어 이름", example = "my-keypair")
    private String keypairName;
}
