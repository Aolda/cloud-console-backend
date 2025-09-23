package com.acc.local.external.dto.glance.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportImageRequest {

    @JsonProperty("method")
    private Method method;

    @JsonProperty("all_stores")
    private Boolean allStores;

    @JsonProperty("all_stores_must_succeed")
    private Boolean allStoresMustSucceed;

    @JsonProperty("stores")
    private List<String> stores;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Method {
        @JsonProperty("name")
        private String name;

        @JsonProperty("uri")
        private String uri;

        @JsonProperty("glance_image_id")
        private String glanceImageId;

        @JsonProperty("glance_region")
        private String glanceRegion;

        @JsonProperty("glance_service_interface")
        private String glanceServiceInterface;
    }
}
