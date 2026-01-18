package com.acc.local.external.adapters.nova;

import com.acc.global.exception.instance.NovaErrorCode;
import com.acc.global.exception.instance.NovaException;
import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
import com.acc.local.external.dto.nova.portInterface.CreateInterfaceRequest;
import com.acc.local.external.modules.nova.NovaPortInterfaceAPIModule;
import com.acc.local.external.ports.NovaServerInterfaceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NovaServerInterfaceExternalAdapter implements NovaServerInterfaceExternalPort {

    private final NovaPortInterfaceAPIModule novaPortInterfaceAPIModule;

    @Override
    public List<InterfaceAttachmentResponse> callListInterfaces(String keystoneToken, String instanceId) {
        ResponseEntity<JsonNode> response;
        try {
            response = novaPortInterfaceAPIModule.listPortInterfaces(keystoneToken, instanceId);
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_RETRIEVAL_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_RETRIEVAL_FAILED);
        }

        return parseInterfaceList(response.getBody());
    }

    @Override
    public InterfaceAttachmentResponse callCreateInterface(String keystoneToken, String instanceId, InterfaceAttachmentRequest request) {
        CreateInterfaceRequest novaRequest = buildCreateInterfaceRequest(request);

        ResponseEntity<JsonNode> response;
        try {
            response = novaPortInterfaceAPIModule.createInterface(keystoneToken, instanceId, novaRequest);
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_CREATION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_CREATION_FAILED);
        }

        return parseInterfaceAttachment(response.getBody());
    }

    @Override
    public void callDetachInterface(String keystoneToken, String instanceId, String interfaceId) {
        ResponseEntity<JsonNode> response;
        try {
            response = novaPortInterfaceAPIModule.detachInterface(keystoneToken, instanceId, interfaceId);
        } catch (WebClientException e) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_DELETION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NovaException(NovaErrorCode.NOVA_SERVER_INTERFACE_DELETION_FAILED);
        }
    }

    private List<InterfaceAttachmentResponse> parseInterfaceList(JsonNode responseBody) {
        List<InterfaceAttachmentResponse> interfaces = new ArrayList<>();
        JsonNode attachmentsNode = responseBody.path("interfaceAttachments");

        if (attachmentsNode.isArray()) {
            for (JsonNode attachmentNode : attachmentsNode) {
                interfaces.add(parseInterfaceFromNode(attachmentNode));
            }
        }

        return interfaces;
    }

    private CreateInterfaceRequest buildCreateInterfaceRequest(InterfaceAttachmentRequest request) {
        CreateInterfaceRequest.CreateInterface.CreateInterfaceBuilder builder = CreateInterfaceRequest.CreateInterface.builder()
                .port_id(request.getInterfaceId())
                .net_id(request.getNetworkId());

        if (request.getFixedIps() != null && !request.getFixedIps().isEmpty()) {
            List<CreateInterfaceRequest.Address> addresses = request.getFixedIps().stream()
                    .map(fixedIp -> CreateInterfaceRequest.Address.builder()
                            .ip_address(fixedIp.getIpAddress())
                            .build())
                    .toList();
            builder.fixed_ips(addresses);
        }

        return CreateInterfaceRequest.builder()
                .interfaceAttachment(builder.build())
                .build();
    }

    private InterfaceAttachmentResponse parseInterfaceAttachment(JsonNode responseBody) {
        JsonNode attachmentNode = responseBody.path("interfaceAttachment");
        return parseInterfaceFromNode(attachmentNode);
    }

    private InterfaceAttachmentResponse parseInterfaceFromNode(JsonNode attachmentNode) {
        List<InterfaceAttachmentResponse.FixedIp> fixedIps = new ArrayList<>();
        JsonNode fixedIpsNode = attachmentNode.path("fixed_ips");
        if (fixedIpsNode.isArray()) {
            for (JsonNode ipNode : fixedIpsNode) {
                fixedIps.add(InterfaceAttachmentResponse.FixedIp.builder()
                        .ipAddress(ipNode.path("ip_address").asText(null))
                        .subnetId(ipNode.path("subnet_id").asText(null))
                        .build());
            }
        }

        return InterfaceAttachmentResponse.builder()
                .interfaceId(attachmentNode.path("port_id").asText(null))
                .networkId(attachmentNode.path("net_id").asText(null))
                .macAddr(attachmentNode.path("mac_addr").asText(null))
                .interfaceState(attachmentNode.path("port_state").asText(null))
                .fixedIps(fixedIps)
                .build();
    }
}

