package com.acc.local.external.dto.nova.serverAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopServerRequest {
    @JsonProperty("os-stop")
    private String osStop = null;
}
