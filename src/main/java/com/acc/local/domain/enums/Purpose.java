package com.acc.local.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;

@Getter
@Schema(description = "인스턴스 유형/목적<br>" +
        "<ul>" +
        "<li><b>GENERAL</b>: General Purpose (general_purpose)</li>" +
        "<li><b>COMPUTE</b>: Compute Optimized (compute_optimized)</li>" +
        "<li><b>MEMORY</b>: Memory Optimized (memory_optimized)</li>" +
        "<li><b>HIGH_CLOCK</b>: High Clock Speed (high_clock_speed)</li>" +
        "</ul>")
public enum Purpose {

    GENERAL("General Purpose", "general_purpose"),
    COMPUTE("Compute Optimized", "compute_optimized"),
    MEMORY("Memory Optimized", "memory_optimized"),
    HIGH_CLOCK("High Clock Speed", "high_clock_speed");

    private final String description;
    private final String code;

    Purpose(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static Purpose findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        return Arrays.stream(values())
                .filter(purpose -> purpose.getCode().equals(code))
                .findFirst()
                .orElse(GENERAL);
    }
}
