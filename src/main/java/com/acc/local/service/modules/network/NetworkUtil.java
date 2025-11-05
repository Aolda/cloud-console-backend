package com.acc.local.service.modules.network;

import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateNetworkRequest;
import org.springframework.stereotype.Component;

@Component
public class NetworkUtil {

    public boolean validateNetworkName(String networkName) {
        return networkName != null && !networkName.isEmpty();
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
}
