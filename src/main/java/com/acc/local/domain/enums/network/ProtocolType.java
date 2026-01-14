package com.acc.local.domain.enums.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum ProtocolType {

    TCP,
    UDP,
    ICMP,
    AH,
    DCCP,
    EGP,
    ESP,
    GRE,
    ICMPV6,
    IGMP,
    IPIP,
    IPV6_ENCAP,
    IPV6_FRAG,
    IPV6_ICMP,
    IPV6_NONXT,
    IPV6_OPTS,
    IPV6_ROUTE,
    OSPF,
    PGM,
    RSVP,
    SCTP,
    ANY,
    UNKNOWN;

    private static final Map<String, ProtocolType> PROTOCOL_TYPE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Enum::name, protocol -> protocol))
    );

    @JsonCreator
    public static ProtocolType findByProtocolName(String protocolName) {
        if (protocolName == null || protocolName.isEmpty() || PROTOCOL_TYPE_MAP.get(protocolName.toUpperCase()) == null) {
            return UNKNOWN;
        }
        String upperProtocolName = protocolName.toUpperCase();

        return PROTOCOL_TYPE_MAP.get(upperProtocolName);
    }


}
