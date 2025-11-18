package com.acc.local.service.modules.network;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.*;
import com.acc.local.external.ports.*;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronModule {

    private final NeutronNetworkExternalPort neutronNetworkExternalPort;
    private final NeutronSubnetExternalPort neutronSubnetExternalPort;
    private final NeutronRouterExternalPort neutronRouterExternalPort;
    private final NeutronPortExternalPort neutronPortExternalPort;
    private final NeutronFloatingIpExternalPort neutronFloatingIpExternalPort;
    private final NeutronSecurityGroupExternalPort neutronSecurityGroupExternalPort;
    private final NeutronSecurityRuleExternalPort neutronSecurityRuleExternalPort;
    private final AuthModule authModule;

    private final int DEFAULT_MTU = 1450;
    private final String DEFAULT_CIDR = "192.168.0.0/24";

    public String createGeneralNetwork(CreateNetworkRequest request, String keystoneToken) {
        return neutronNetworkExternalPort.callCreateGeneralNetwork(keystoneToken,
                request.getNetworkName(),
                request.getDescription(),
                request.getMtu());
    }

    public boolean canDeleteNetwork(String keystoneToken, String networkId) {
        Map<String, String> network = neutronNetworkExternalPort.getNetworkNameAndId(keystoneToken, networkId);
        return !network.get("name").equals("default-network");
    }

    public void deleteNetwork(String keystoneToken, String networkId) {
        neutronNetworkExternalPort.callDeleteNetwork(keystoneToken, networkId);
    }

    public PageResponse<ViewNetworksResponse> listNetworks(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return neutronNetworkExternalPort.callListNetworks(keystoneToken, projectId, marker, direction, limit);
    }

    public String getProviderNetworkId(String keystoneToken) {
        return neutronNetworkExternalPort.getProviderNetwork(keystoneToken).get("id");
    }

    public void createDefaultNetwork(String keystoneToken) {
        String id = neutronNetworkExternalPort.callCreateGeneralNetwork(
                keystoneToken,
                "default-network",
                null,
                DEFAULT_MTU
        );

        createSubnet(
                keystoneToken,
                List.of(
                        CreateNetworkRequest.Subnet.builder().
                                cidr(DEFAULT_CIDR).
                                subnetName("default-subnet").
                                build()
                ),
                id
        );
    }

    public String getDefaultNetworkId(String keystoneToken, String projectId) {
        return neutronNetworkExternalPort.callListNetworksByNetworkName(
                keystoneToken,
                projectId,
                "default-network"
        ).getFirst();
    }

    public void createSubnet(String keystoneToken, List<CreateNetworkRequest.Subnet> subnets, String networkId) {
        neutronSubnetExternalPort.callCreateSubnet(keystoneToken, subnets, networkId);
    }

    /* --- Routers --- */
    public PageResponse<ViewRoutersResponse> listRouters(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return neutronRouterExternalPort.callListRouters(keystoneToken, projectId, marker, direction, limit);
    }

    public void deleteRouter(String keystoneToken, String routerId) {
        neutronRouterExternalPort.callDeleteRouter(keystoneToken, routerId);
    }

    public void createRouter(String keystoneToken, String routerName, boolean isExternal) {
        String networkId = null;
        if (isExternal) {
            networkId = getProviderNetworkId(keystoneToken);
        }
        neutronRouterExternalPort.callCreateRouter(keystoneToken, routerName, networkId);
    }

    /* --- External IPs --- */
    public boolean allocateExternalIpToInterface(String keystoneToken, String floatingNetworkId, String portId) {
        try {
            neutronFloatingIpExternalPort.allocateFloatingIpToPort(keystoneToken, floatingNetworkId, portId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void releaseExternalIpFromInterface(String keystoneToken, String ExternalIpId) {
        neutronFloatingIpExternalPort.releaseFloatingIpFromPort(keystoneToken, ExternalIpId);
    }

    /* --- Interfaces --- */
    public String createInterface(String keystoneToken, CreateInterfaceRequest request) {
        return neutronPortExternalPort.callCreatePort(keystoneToken,
                request.getNetworkId(),
                request.getInterfaceName(),
                request.getSubnetId(),
                request.getSecurityGroupIds(),
                request.getDescription()).get("id");
    }

    public Map<String, String> getExternalIpByInterfaceId(String keystoneToken, String interfaceId) {
        return neutronFloatingIpExternalPort.getFloatingIpInfo(keystoneToken, interfaceId);
    }

    public void deleteInterface(String keystoneToken, String portId) {
        neutronPortExternalPort.callDeletePort(keystoneToken, portId);
    }

    public PageResponse<ViewInterfacesResponse> listInterfaces(String keystoneToken, String projectId, String marker, String direction, int limit, String instanceId, String networkId) {
        return neutronPortExternalPort.callListPorts(keystoneToken, projectId, marker, direction, limit, instanceId, networkId);
    }

    /* --- Security Groups --- */
    public void createSecurityGroup(String keystoneToken, String projectId, String securityGroupName, String description) {
        neutronSecurityGroupExternalPort.callCreateSecurityGroup(keystoneToken, projectId, securityGroupName, description);
    }

    public PageResponse<ViewSecurityGroupsResponse> listSecurityGroups(String keystoneToken, String projectId, String marker, String direction, int limit) {
        return neutronSecurityGroupExternalPort.callListSecurityGroups(keystoneToken, projectId, marker, direction, limit);
    }

    public ViewSecurityGroupsResponse getSecurityGroupDetails(String keystoneToken, String securityGroupId, String marker, String direction, int limit) {
        return neutronSecurityGroupExternalPort.callGetSecurityGroupById(keystoneToken, securityGroupId, marker, direction, limit);
    }

    public void deleteSecurityGroup(String keystoneToken, String securityGroupId) {
        neutronSecurityGroupExternalPort.callDeleteSecurityGroup(keystoneToken, securityGroupId);
    }

    /* --- Security Rules --- */
    public void createSecurityGroupRule(String keystoneToken, String sgId, String direction, String protocol, Integer port, String remoteGroupId, String remoteIpPrefix) {
        neutronSecurityRuleExternalPort.callCreateSecurityRule(
                keystoneToken,
                sgId,
                direction,
                protocol,
                port,
                remoteGroupId,
                remoteIpPrefix
        );
    }

    public void deleteSecurityGroupRule(String keystoneToken, String srId) {
        neutronSecurityRuleExternalPort.callDeleteSecurityRule(keystoneToken, srId);
    }

    public void setDefaultSecurityGroup(String keystoneToken, String projectId) {
        ViewSecurityGroupsResponse defaultSg = neutronSecurityGroupExternalPort.callGetSecurityGroupByName(keystoneToken, projectId, "default");

        neutronSecurityRuleExternalPort.callCreateSecurityRule(keystoneToken,
                defaultSg.getSecurityGroupId(),
                "ingress",
                "tcp",
                22,
                null,
                "0.0.0.0/0"
                );

        neutronSecurityRuleExternalPort.callCreateSecurityRule(keystoneToken,
                defaultSg.getSecurityGroupId(),
                "egress",
                "tcp",
                22,
                null,
                "0.0.0.0/0"
        );

        neutronSecurityRuleExternalPort.callCreateSecurityRule(keystoneToken,
                defaultSg.getSecurityGroupId(),
                "ingress",
                "tcp",
                80,
                null,
                "0.0.0.0/0");

        neutronSecurityRuleExternalPort.callCreateSecurityRule(keystoneToken,
                defaultSg.getSecurityGroupId(),
                "ingress",
                "tcp",
                443,
                null,
                "0.0.0.0/0");
    }
}
