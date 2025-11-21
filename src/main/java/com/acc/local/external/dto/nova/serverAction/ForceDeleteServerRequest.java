package com.acc.local.external.dto.nova.serverAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForceDeleteServerRequest {
    private String forceDelete = null;
}
