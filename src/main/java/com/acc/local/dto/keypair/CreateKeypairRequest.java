package com.acc.local.dto.keypair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKeypairRequest {

    @Schema(description = "생성할 키페어의 이름", example = "my-keypair")
    private String keypairName;
}
