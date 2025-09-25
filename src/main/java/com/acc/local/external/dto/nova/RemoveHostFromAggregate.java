package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveHostFromAggregate {
   private RemoveHost remove_host;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RemoveHost {
        private String host;
    }
}
