package com.acc.local.external.dto.nova;

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
public class CreateServerRequest {
    private Server server;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Server {
        private String name;
        private String imageRef;
        private String flavorRef;
        private String adminPass;
        private String key_name;
        private List<String> security_groups;
        private List<Network> networks;
        private Map<String, String> metadata;
        private String user_data;
        private String availability_zone;
        private Boolean config_drive;
        private String diskConfig; // 변수명 변환 필요
        private String accessIPv4;
        private String accessIPv6;
        private String hostname;
        private String tags;
        private String hypervisor_hostname;
        private Integer minCount;
        private Integer maxCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Network {
        private String uuid;
        private String port;
        private String fixed_ip;
        private String tag;
    }
} 