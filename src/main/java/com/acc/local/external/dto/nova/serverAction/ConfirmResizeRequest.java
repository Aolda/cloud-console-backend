package com.acc.local.external.dto.nova.serverAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmResizeRequest {
    private String confirmResize = null;
}
