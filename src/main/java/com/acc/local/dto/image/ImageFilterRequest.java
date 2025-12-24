package com.acc.local.dto.image;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFilterRequest {
    private Boolean hidden;      // default=false
    private Boolean isActive;    // null/true → status=active, false → status=queued|saving|killed|deleted
    private String visibility;   // null / (public | private)
    private String name;         // exact match
    private String architecture; // exact architecture

}
