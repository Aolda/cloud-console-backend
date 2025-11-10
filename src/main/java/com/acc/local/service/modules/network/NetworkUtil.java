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

    public boolean validateSubnetCidr(String subnetCidr) {
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


}
