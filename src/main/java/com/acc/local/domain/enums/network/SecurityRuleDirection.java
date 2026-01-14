package com.acc.local.domain.enums.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SecurityRuleDirection {
    INGRESS("들어오는 트래픽"),
    EGRESS("나가는 트래픽");

    private final String description;

    @JsonValue
    public static String getLowerCaseName(SecurityRuleDirection direction) {
        return direction.name().toLowerCase();
    }

    @JsonCreator
    public static SecurityRuleDirection findByDirectionName(String directionName) {
        if (directionName == null || directionName.isEmpty()) {
            return null;
        }
        String upperDirectionName = directionName.toUpperCase();

        for (SecurityRuleDirection direction : values()) {
            if (direction.name().equals(upperDirectionName)) {
                return direction;
            }
        }
        return null;
    }
}
