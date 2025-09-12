package com.acc.local.external.dto.storage.attachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteAttachmentRequest {

    @JsonProperty("os-complete")
    private Object osComplete;
}
