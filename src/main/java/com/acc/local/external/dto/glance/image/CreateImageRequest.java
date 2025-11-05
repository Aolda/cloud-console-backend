package com.acc.local.external.dto.glance.image;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateImageRequest {

    @JsonProperty("container_format")
    private String containerFormat;

    @JsonProperty("disk_format")
    private String diskFormat;

    @JsonProperty("id")
    private String id;

    @JsonProperty("min_disk")
    private Integer minDisk;

    @JsonProperty("min_ram")
    private Integer minRam;

    /** Image name. */
    @JsonProperty("name")
    private String name;

    @JsonProperty("protected")
    private Boolean deleteProtected;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("visibility")
    private String visibility;

}
