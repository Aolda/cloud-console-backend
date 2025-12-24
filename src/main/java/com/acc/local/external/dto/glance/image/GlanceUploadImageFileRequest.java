package com.acc.local.external.dto.glance.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlanceUploadImageFileRequest {

    @JsonProperty("X-Image-Meta-Store")
    private String xImageMetaStore;

    @JsonProperty("x-openstack-image-size")
    private String xOpenstackImageSize;

    @JsonProperty("data")
    private byte[] data;
}
