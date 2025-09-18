package com.acc.local.external.dto.cinder.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConnectorDto {
    private String initiator;
    private String ip;
    private String platform;
    private String host;

    @JsonProperty("os_type")
    private String osType;

    private Boolean multipath;
    private String mountpoint;
    private String mode;

    @JsonProperty("do_local_attach")
    private Boolean doLocalAttach;
}
