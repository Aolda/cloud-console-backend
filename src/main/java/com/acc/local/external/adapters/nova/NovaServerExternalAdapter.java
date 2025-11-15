package com.acc.local.external.adapters.nova;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceResponse;
import com.acc.local.domain.enums.InstanceStatus;
import com.acc.local.external.dto.nova.server.CreateServerRequest;
import com.acc.local.external.modules.nova.NovaServerAPIModule;
import com.acc.local.external.ports.NovaServerExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class NovaServerExternalAdapter implements NovaServerExternalPort {

    private final NovaServerAPIModule novaServerAPIModule;

    @Override
    public PageResponse<InstanceResponse> callListInstances(String keystoneToken, String projectId, String marker, String direction, int limit) {
        ResponseEntity<JsonNode> response;

        try {
            response = novaServerAPIModule.listServersDetail(keystoneToken, getListServersParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0));
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
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
            throw new NovaException(NovaErrorCode.NOVA_SERVER_CREATION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_CREATION_FAILED);
        }
    }

    private Map<String, String> getListServersParams(String projectId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>(Map.of(
                "project_id", projectId,
                "page_reverse", direction.equals("prev") ? "true" : "false",
                "limit", String.valueOf(limit)
        ));

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
        }
        return params;
    }

    private List<InstanceResponse> parseServers(ResponseEntity<JsonNode> response) {
        List<InstanceResponse> servers = new ArrayList<>();
        JsonNode serversNode = response.getBody().path("servers");
        for (JsonNode serverNode : serversNode) {
            JsonNode imageNode = serverNode.path("image");
            List<String> internalIps = new ArrayList<>();
            List<String> externalIps = new ArrayList<>();

            serverNode.path("addresses").fields().forEachRemaining(entry ->
                    StreamSupport.stream(entry.getValue().spliterator(), false)
                            .forEach(addr -> {
                                String ipType = addr.path("OS-EXT-IPS:type").asText();
                                if ("fixed".equals(ipType)) {
                                    internalIps.add(addr.path("addr").asText());
                                } else if ("floating".equals(ipType)) {
                                    externalIps.add(addr.path("addr").asText());
                                }
                            })
            );

            servers.add(InstanceResponse.builder()
                    .instanceId(serverNode.path("id").asText())
                    .instanceName(serverNode.path("name").asText())
                    .status(InstanceStatus.findByStatusName(serverNode.path("status").asText()))
                    .type(serverNode.at("/flavor/original_name").asText())
                    .image(imageNode.isObject() ? imageNode.path("id").asText() : imageNode.asText())
                    .internalIps(internalIps)
                    .externalIps(externalIps)
                    .build());
        }
        return servers;
    }

    private PageResponse<InstanceResponse> getServersPageResponse(String marker, int limit, List<InstanceResponse> servers) {
        boolean hasNext = limit > 0 && servers.size() > limit;
        if (hasNext) {
            servers.remove(limit);
        }

        return PageResponse.<InstanceResponse>builder()
                .contents(servers)
                .nextMarker(hasNext ? servers.getLast().getInstanceId() : null)
                .prevMarker(marker != null && !servers.isEmpty() ? servers.getFirst().getInstanceId() : null)
                .last(!hasNext)
                .first(marker == null)
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
        List<CreateServerRequest.BlockDeviceMappingV2> blockDeviceMapping = null;
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
        }

        CreateServerRequest.Server serverPayload = CreateServerRequest.Server.builder()
                .name(instanceRequest.getInstanceName())
                .imageRef(imageRef)
                .flavorRef(instanceRequest.getTypeId())
                .adminPass(instanceRequest.getPassword())
                .key_name(instanceRequest.getKeypairId())
                .networks(networks.isEmpty() ? null : networks)
                .security_groups(securityGroups)
                .block_device_mapping_v2(blockDeviceMapping)
                .build();

        return CreateServerRequest.builder()
                .server(serverPayload)
                .build();
    }
}
