package com.acc.local.external.adapters.flavor;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.type.FlavorExternalErrorCode;
import com.acc.global.exception.type.FlavorExternalException;
import com.acc.local.domain.enums.Architecture;
import com.acc.local.domain.enums.Purpose;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
import com.acc.local.external.dto.nova.flavor.CreateFlavorRequest;
import com.acc.local.external.dto.nova.flavor.FlavorExtraSpecsRequest;
import com.acc.local.external.modules.nova.NovaFlavorAPIModule;
import com.acc.local.external.modules.nova.NovaFlavorExtraSpecsModule;
import com.acc.local.external.ports.NovaFlavorExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlavorExternalAdapter implements NovaFlavorExternalPort {

    private final NovaFlavorAPIModule novaFlavorAPIModule;
    private final NovaFlavorExtraSpecsModule novaFlavorExtraSpecsModule;

    private static final String KEY_ARCH = ":architecture";
    private static final String KEY_CATEGORY = ":category";
    private static final String KEY_BANDWIDTH = ":bandwidth";
    private static final String KEY_USB = ":usb_enabled";
    private static final String KEY_NUMA = "hw:numa_nodes";


    @Override
    public PageResponse<InstanceTypeResponse> callListFlavors(String keystoneToken, String architect, String marker, String direction, int limit) {
        ResponseEntity<JsonNode> response;

        try {
            response = novaFlavorAPIModule.listFlavorsDetails(keystoneToken, getListFlavorsParams(marker, direction, limit == 0 ? 0 : limit + 1));
        } catch (WebClientException e) {
            throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_RETRIEVAL_FAILED);
        }

        List<InstanceTypeResponse> flavors = parseFlavors(response);
        if (architect != null && !architect.isEmpty()) {
            flavors = filterByArchitect(flavors, architect);
        }
        return getFlavorsPageResponse(marker, limit, flavors);
    }

    @Override
    public void callCreateFlavor(String keystoneToken, InstanceTypeCreateRequest request) {
        CreateFlavorRequest createRequest = buildCreateFlavorRequest(request);
        ResponseEntity<JsonNode> createResponse;

        /* ---  Flavor 생성 --- */
        try {
            createResponse = novaFlavorAPIModule.createFlavor(keystoneToken, null, createRequest);
        } catch (WebClientException e) {
            if (e instanceof WebClientResponseException ex) {
                if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                    throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_ALREADY_EXISTS);
                }
                if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    log.error(ex.getResponseBodyAsString());
                    throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_INVALID_REQUEST);
                }
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_UNAUTHORIZED);
                }
                if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                    throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_FORBIDDEN);
                }
            }
            throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_CREATION_FAILED);
        }

        String flavorId = createResponse.getBody().path("flavor").path("id").asText();
        log.info("Flavor created successfully. ID: {}", flavorId);

        try {
            /* ---  Flavor Extra Specs 생성 --- */
            FlavorExtraSpecsRequest specsRequest = buildExtraSpecsRequest(request);
            novaFlavorExtraSpecsModule.createExtraSpecs(keystoneToken, flavorId, specsRequest);

        } catch (WebClientException e) {
            log.error("Failed to configure Specs for Flavor ID: {}. Initiating Rollback", flavorId, e);
            try {
                novaFlavorAPIModule.deleteFlavor(keystoneToken, flavorId);
                log.info("Rollback successful: Flavor {} has been deleted.", flavorId);
            } catch (Exception rollbackEx) {
                log.error("CRITICAL: Rollback FAILED for Flavor {}. Manual deletion required.", flavorId, rollbackEx);
            }
            throw new FlavorExternalException(FlavorExternalErrorCode.FLAVOR_EXTERNAL_CREATION_FAILED);
        }
    }

    private Map<String, String> getListFlavorsParams(String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put("is_public", "None");  // Public + Private 모두 조회

        if (limit != 0) {
            params.put("limit", String.valueOf(limit));
        }

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
            params.put("page_reverse", direction.equals("prev") ? "true" : "false");
        }
        return params;
    }

    private List<InstanceTypeResponse> parseFlavors(ResponseEntity<JsonNode> response) {
        List<InstanceTypeResponse> flavors = new ArrayList<>();
        JsonNode flavorsNode = response.getBody().path("flavors");

        for (JsonNode flavorNode : flavorsNode) {
            JsonNode specs = flavorNode.path("extra_specs");
            flavors.add(InstanceTypeResponse.builder()
                    .typeId(flavorNode.path("id").asText())
                    .typeName(flavorNode.path("name").asText())
                    .core(flavorNode.path("vcpus").asInt())
                    .ram(flavorNode.path("ram").asInt())
                    .diskSize(flavorNode.path("disk").asInt())
                    .isPublic(flavorNode.path("os-flavor-access:is_public").asBoolean())
                    .description(flavorNode.path("description").asText(null))
                    .architect(Architecture.findByCode(getText(specs, KEY_ARCH)))
                    .purpose(Purpose.findByCode(getText(specs, KEY_CATEGORY)))
                    .bandwidth(asIntOrNull(specs.path(KEY_BANDWIDTH)))
                    .usb(asBooleanOrNull(specs.path(KEY_USB)))
                    .numa(asIntOrNull(specs.path(KEY_NUMA)))
                    .build());
        }
        return flavors;
    }

    private List<InstanceTypeResponse> filterByArchitect(List<InstanceTypeResponse> flavors, String architect) {

        Architecture targetArch = Architecture.findByName(architect);
        if (targetArch != null) {
            return flavors.stream()
                    .filter(f -> f.getArchitect() == targetArch)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private CreateFlavorRequest buildCreateFlavorRequest(InstanceTypeCreateRequest instanceTypeCreateRequest) {
        // 콘솔 입력값(GiB) -> OpenStack (MiB)
        int ramInMib = instanceTypeCreateRequest.getRam() * 1024;

        CreateFlavorRequest.Flavor flavor = CreateFlavorRequest.Flavor.builder()
                .name(instanceTypeCreateRequest.getTypeName())
                .vcpus(instanceTypeCreateRequest.getCore())
                .ram(ramInMib)
                .disk(instanceTypeCreateRequest.getDiskSize())
                .isPublic(true)
                .description("Created by ACC")
                .build();

        return CreateFlavorRequest.builder()
                .flavor(flavor)
                .build();
    }

    private FlavorExtraSpecsRequest buildExtraSpecsRequest(InstanceTypeCreateRequest request) {
        Map<String, String> specs = new HashMap<>();

        if (request.getArchitect() != null) specs.put(KEY_ARCH, request.getArchitect().getCode());
        if (request.getPurpose() != null) specs.put(KEY_CATEGORY, request.getPurpose().getCode());
        if (request.getBandwidth() != null) specs.put(KEY_BANDWIDTH, String.valueOf(request.getBandwidth()));
        if (request.getUsb() != null) specs.put(KEY_USB, String.valueOf(request.getUsb()));
        if (request.getNuma() != null) specs.put(KEY_NUMA, String.valueOf(request.getNuma()));

        return FlavorExtraSpecsRequest.builder()
                .extraSpecs(specs)
                .build();
    }

    private PageResponse<InstanceTypeResponse> getFlavorsPageResponse(String marker, int limit, List<InstanceTypeResponse> flavors) {
        int returnedSize = flavors.size();
        if (limit != 0 && returnedSize == limit + 1) {
            flavors.removeLast();
        }

        return PageResponse.<InstanceTypeResponse>builder()
                .contents(flavors)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : flavors.getLast().getTypeId())
                .prevMarker(limit == 0 || marker == null ? null : flavors.getFirst().getTypeId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(flavors.size())
                .build();
    }

    private String getText(JsonNode node, String key) {
        JsonNode target = node.path(key);
        return target.isMissingNode() || target.isNull() ? null : target.asText();
    }

    private Integer asIntOrNull(JsonNode node) {
        return node.isMissingNode() ? null : Integer.parseInt(node.asText());
    }

    private Boolean asBooleanOrNull(JsonNode node) {
        return node.isMissingNode() ? null : Boolean.parseBoolean(node.asText());
    }
}
