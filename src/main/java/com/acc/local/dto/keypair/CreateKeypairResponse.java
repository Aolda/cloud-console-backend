package com.acc.local.dto.keypair;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKeypairResponse {

    @Schema(description = "생성된 키페어의 이름", example = "my-keypair")
    private String keypairName;

    @Schema(description = "키페어의 핑거프린트 (ID로 사용됨)", example = "aa:bb:cc:dd:ee:ff:11:22:33:44:55:66:77:88:99:00")
    private String fingerprint;

    @Schema(description = "생성된 공개 키", example = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQ...")
    private String publicKey;

    @Schema(description = "생성된 개인 키 (생성 응답 시에만 반환됨)", example = "-----BEGIN RSA PRIVATE KEY-----\nMIIE... \n-----END RSA PRIVATE KEY-----")
    private String privateKey;
}
