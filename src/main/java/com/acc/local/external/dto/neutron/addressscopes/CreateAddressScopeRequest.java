package com.acc.local.external.dto.neutron.addressscopes;

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
public class CreateAddressScopeRequest {
    @JsonProperty("address_scope")
    private AddressScope addressScope;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddressScope {
        private String name;
        @JsonProperty("ip_version")
        private Integer ipVersion;
        private Boolean shared;
        @JsonProperty("project_id")
        private String projectId;
    }
}
