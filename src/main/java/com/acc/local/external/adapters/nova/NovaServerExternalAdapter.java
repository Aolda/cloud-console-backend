package com.acc.local.external.adapters.nova;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.domain.enums.InstanceStatus;
import com.acc.local.external.dto.nova.server.CreateServerRequest;
import com.acc.local.external.dto.nova.response.NovaServersResponse;
import com.acc.local.external.modules.nova.NovaServerAPIModule;
import com.acc.local.external.ports.NovaServerExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class NovaServerExternalAdapter implements NovaServerExternalPort {

    private final NovaServerAPIModule novaServerAPIModule;

    @Override
    public PageResponse<InstanceResponse> callListInstances(String keystoneToken, String projectId, String marker, String direction, int limit) {
        ResponseEntity<NovaServersResponse> response;

        try {
            response = novaServerAPIModule.listServersDetail(keystoneToken, getListServersParams(projectId, marker, direction, limit == 0 ? 0 : limit + 1));
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Nova API non-success status: {}, body: {}", response.getStatusCode(), response.getBody());
            throw new NovaException(NovaErrorCode.NOVA_SERVER_RETRIEVAL_FAILED);
        }

        List<InstanceResponse> servers = parseServers(response);
        return getServersPageResponse(marker, limit, servers);
    }

    @Override
    public void callCreateInstance(String token, String projectId, InstanceCreateRequest request) {
        CreateServerRequest novaCreateServerRequest = buildNovaCreateServerRequest(request);

        ResponseEntity<JsonNode> response;
        try {
            response = novaServerAPIModule.createServer(token, novaCreateServerRequest);
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_CREATION_FAILED, e.getMessage());
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Nova API non-success status: {}, body: {}", response.getStatusCode(), response.getBody());
            throw new NovaException(NovaErrorCode.NOVA_SERVER_CREATION_FAILED);
        }

    }

    private Map<String, String> getListServersParams(String projectId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>(Map.of(
                "project_id", projectId
        ));

        if (limit != 0) {
            params.put("limit", String.valueOf(limit));
        }

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
            params.put("page_reverse", direction.equals("prev") ? "true" : "false");
        }
        return params;
    }

    private List<InstanceResponse> parseServers(ResponseEntity<NovaServersResponse> response) {
            List<InstanceResponse> servers = new ArrayList<>();
            for (NovaServersResponse.Server server : response.getBody().getServers()) {
                List<String> internalIps = new ArrayList<>();
                List<String> externalIps = new ArrayList<>();

                // addresses Map을 순회하며 IP 주소 추출
                if (server.getAddresses() != null) {
                    server.getAddresses().values().forEach(addressList ->
                            addressList.forEach(addr -> {
                                if ("fixed".equals(addr.getType())) {
                                    internalIps.add(addr.getAddr());
                                } else if ("floating".equals(addr.getType())) {
                                    externalIps.add(addr.getAddr());
                                }
                            })
                    );
                }

                // image는 Object 타입이므로 처리
                String imageId = extractImageId(server.getImage());

                servers.add(InstanceResponse.builder()
                        .instanceId(server.getId())
                        .instanceName(server.getName())
                        .status(InstanceStatus.findByStatusName(server.getStatus()))
                        .type(extractFlavorType(server.getFlavor()))
                        .image(imageId)
                        .internalIps(internalIps)
                        .externalIps(externalIps)
                        .build());
            }
            return servers;
    }

    private String extractImageId(Object image) {
        if (image == null) {
            return null;
        }
        if (image instanceof String) {
            return (String) image;
        }
        if (image instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> imageMap = (Map<String, Object>) image;
            Object id = imageMap.get("id");
            return id != null ? id.toString() : null;
        }
        return null;
    }

    private String extractFlavorType(NovaServersResponse.Flavor flavor) {
        if (flavor == null) {
            return null;
        }
        // original_name이 있으면 사용, 없으면 name으로 fallback
        String originalName = flavor.getOriginalName();
        return (originalName != null && !originalName.isEmpty())
                ? originalName
                : flavor.getName();
    }

    private PageResponse<InstanceResponse> getServersPageResponse(String marker, int limit, List<InstanceResponse> servers) {
        int returnedSize = servers.size();
        if (limit != 0 && returnedSize == limit + 1) {
            servers.removeLast();
        }

        return PageResponse.<InstanceResponse>builder()
                .contents(servers)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : servers.getLast().getInstanceId())
                .prevMarker(limit == 0 || marker == null ? null : servers.getFirst().getInstanceId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(servers.size())
                .build();
    }

    private CreateServerRequest buildNovaCreateServerRequest(InstanceCreateRequest instanceRequest) {
        // networks and interfaces
        List<CreateServerRequest.Network> networks = new ArrayList<>();
        if (instanceRequest.getNetworkIds() != null) {
            instanceRequest.getNetworkIds().forEach(netId ->
                    networks.add(CreateServerRequest.Network.builder().uuid(netId).build())
            );
        }
        if (instanceRequest.getInterfaceIds() != null) {
            instanceRequest.getInterfaceIds().forEach(portId ->
                    networks.add(CreateServerRequest.Network.builder().port(portId).build())
            );
        }

        // security groups (OpenStack API -> 'name')
        List<CreateServerRequest.SecurityGroup> securityGroups = null;
        if (instanceRequest.getSecurityGroupIds() != null && !instanceRequest.getSecurityGroupIds().isEmpty()) {
            securityGroups = instanceRequest.getSecurityGroupIds().stream()
                    .map(sgId -> CreateServerRequest.SecurityGroup.builder().name(sgId).build())
                    .toList();
        }

        // volume
        String imageRef = instanceRequest.getImageId();
        List<CreateServerRequest.BlockDeviceMappingV2> blockDeviceMapping;
        Integer diskSize = instanceRequest.getDiskSize();

        // boot-from-volume
        if (diskSize != null && diskSize > 0) {
            imageRef = null;

            CreateServerRequest.BlockDeviceMappingV2 bootVolume = CreateServerRequest.BlockDeviceMappingV2.builder()
                    .uuid(instanceRequest.getImageId())
                    .source_type("image")
                    .destination_type("volume")
                    .volume_size(diskSize)
                    .boot_index(0)
                    .delete_on_termination(true)
                    .build();

            blockDeviceMapping = List.of(bootVolume);
        } else {
            blockDeviceMapping = List.of(); // 빈 배열로 설정
        }

        CreateServerRequest.Server serverPayload = CreateServerRequest.Server.builder()
                .name(instanceRequest.getInstanceName())
                .imageRef(imageRef)
                .flavorRef(instanceRequest.getTypeId())
                .adminPass(instanceRequest.getPassword())
                .key_name(instanceRequest.getKeypairName())
                .networks(networks.isEmpty() ? null : networks)
                .security_groups(securityGroups)
                .block_device_mapping_v2(blockDeviceMapping)
                .build();

        return CreateServerRequest.builder()
                .server(serverPayload)
                .build();
    }
}
