package com.acc.local.service.modules.type;

import org.springframework.stereotype.Component;

@Component
public class InstanceTypeUtil {
    public boolean validateInstanceTypeName(String typeName) {
        return typeName != null && !typeName.isEmpty() &&
                typeName.matches("^[a-zA-Z0-9._-]{1,255}$");
    }
}
