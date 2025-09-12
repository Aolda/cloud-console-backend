package com.acc.local.external.dto.storage.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    private String host;                   // for freeze, thaw, failover_host
    private String binary;                 // for disable, enable, log, set-log
    private String server;                 // optional, get-log/set-log
    private String prefix;                 // optional, get-log/set-log
    @JsonProperty("disabled_reason")
    private String disabledReason;         // optional, disable-log-reason
    @JsonProperty("backend_id")
    private String backendId;              // optional, failover_host
    private String cluster;                // optional, failover_host
    private String level;                  // optional, set-log
}
