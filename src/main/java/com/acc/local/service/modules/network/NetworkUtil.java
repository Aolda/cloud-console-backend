package com.acc.local.service.modules.network;

import org.springframework.stereotype.Component;

@Component
public class NetworkUtil {

    public boolean validateResourceName(String resourceName) {

        return resourceName != null && !resourceName.isEmpty() &&
                resourceName.matches("^[a-zA-Z][0-9a-zA-Z\\-_()\\[\\]\\.:^]{0,127}$");
    }

    public boolean validateNetworkMtu(int mtu) {
        return mtu > 68 && mtu <= 65535;
    }

    public boolean validateCidr(String subnetCidr) {
        return subnetCidr != null && !subnetCidr.isEmpty() &&
                subnetCidr.
                        matches("^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                                "){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\/" +
                                "(?:[1-9]|[12]\\d|3[0-2])$");
    }

    public boolean validateSubnetName(String subnetName) {
        return subnetName != null && !subnetName.isEmpty();
    }

    public boolean validateGateway(Boolean gateway) {
        return gateway != null;
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public boolean isNullOrEmpty(java.util.List<?> list) {
        return list == null || list.isEmpty();
    }

    public boolean isNullOrEmpty(Boolean bool) {
        return bool == null;
    }

    public String validateProtocol(String protocol) {
        if (protocol == null || protocol.isEmpty()) {
            return "any";
        }
        return switch (protocol.toLowerCase()) {
            case "any",
                 "ah",
                 "dccp",
                 "egp",
                 "esp",
                 "gre",
                 "icmpv6",
                 "igmp",
                 "ipip",
                 "ipv6-encap",
                 "ipv6-frag",
                 "ipv6-icmp",
                 "ipv6-nonxt",
                 "ipv6-opts",
                 "ipv6-route",
                 "ospf",
                 "pgm",
                 "rsvp",
                 "sctp",
                 "tcp",
                 "udp",
                 "icmp" -> protocol.toLowerCase();
            default -> null;
        };
    }

    public String validateDirection(String direction) {
        if (direction == null || direction.isEmpty()) {
            return null;
        }
        return switch (direction.toLowerCase()) {
            case "ingress" -> "ingress";
            case "egress" -> "egress";
            default -> null;
        };
    }

    public boolean validatePortRange(Integer port) {
        return port != null && port >= 0 && port <= 65535;
    }

    public boolean hasValidRemoteSecurityGroupIdOrCidr(String remoteSecurityGroupId, String cidr) {
        return (remoteSecurityGroupId != null && !remoteSecurityGroupId.isEmpty()) ||
                ((cidr != null && !cidr.isEmpty()) && validateCidr(cidr));
    }

    public boolean isDefaultSecurityGroup(String securityGroupName) {
        return securityGroupName.equals("default");
    }
}
