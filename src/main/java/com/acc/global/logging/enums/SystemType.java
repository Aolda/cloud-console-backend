package com.acc.global.logging.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemType {

    OPENSTACK("오픈스택"),
    GOOGLE("구글"),
    DISCORD("디스코드"),
    APM("APM"),
    KEYCLOAK("키클락"),
    UNKNOWN("알 수 없음");

    private final String description;

    public static SystemType findByName(String name) {
        if (name == null || name.isEmpty()) {
            return UNKNOWN;
        }

        for (SystemType systemType : SystemType.values()) {
            if (systemType.name().equalsIgnoreCase(name)) {
                return systemType;
            }
        }

        for (OpenstackComponents component : OpenstackComponents.values()) {
            if (component.name().equalsIgnoreCase(name)) {
                return OPENSTACK;
            }
        }

        return UNKNOWN;
    }

    enum OpenstackComponents {
        NOVA,
        NEUTRON,
        CINDER,
        GLANCE,
        KEYSTONE,
        SWIFT,
        HORIZON,
        HEAT,
        TROVE,
        MANILA,
        MAGNUM,
        Ironic
    }
}
