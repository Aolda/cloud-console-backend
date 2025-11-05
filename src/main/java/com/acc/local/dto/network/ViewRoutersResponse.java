package com.acc.local.dto.network;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewRoutersResponse {

    String routerName;
    String routerId;
    String status;
    Boolean gateway;
    String externalNetworkName;
    String externalIp;
    String createdAt;

}
