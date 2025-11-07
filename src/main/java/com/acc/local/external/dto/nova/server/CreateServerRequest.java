package com.acc.local.external.dto.nova.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateServerRequest {
    private Server server;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Server {
        private String name;
        private String imageRef;
        private String flavorRef;
        private String adminPass;
        private String key_name;
        private List<SecurityGroup> security_groups;
        private List<Network> networks;
        private Map<String, String> metadata;
        private String user_data;
        private String availability_zone;
        private Boolean config_drive;
        @JsonProperty("OS-DCF:diskConfig")
        private String diskConfig;
        private String accessIPv4;
        private String accessIPv6;
        private String hostname;
        private String tags;
        private String hypervisor_hostname;
        private Integer minCount;
        private Integer maxCount;
        private List<BlockDeviceMappingV2> block_device_mapping_v2;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Network {
        private String uuid;
        private String port;
        private String fixed_ip;
        private String tag;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SecurityGroup {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BlockDeviceMappingV2 {
        private String source_type;
        private String destination_type;
        private String uuid;
        private Integer boot_index;
        private Integer volume_size;
        private Boolean delete_on_termination;
    }
} 
