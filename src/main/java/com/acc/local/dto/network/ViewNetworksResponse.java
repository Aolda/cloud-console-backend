    package com.acc.local.dto.network;

    import com.fasterxml.jackson.annotation.JsonInclude;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;

    @Builder
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class ViewNetworksResponse {
        String networkId;
        String networkName;
        List<Subnet> subnets;

        @Builder
        @Getter
        @Setter
        public static class Subnet {
            String subnetId;
            String subnetName;
            String cidr;
        }
    }
