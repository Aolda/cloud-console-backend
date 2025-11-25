package com.acc.local.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;

@Getter
@Schema(description = "아키텍처 타입<br>" +
        "<ul>" +
        "<li><b>X86</b>: X86 Architecture (x86_architecture)</li>" +
        "<li><b>HETEROGENEOUS</b>: Heterogeneous Computing (heterogeneous_computing)</li>" +
        "</ul>")
public enum Architecture {

    X86("X86 Architecture", "x86_architecture"),
    HETEROGENEOUS("Heterogeneous Computing", "heterogeneous_computing");

    private final String description;
    private final String code;

    Architecture(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public static Architecture findByCode(String code) {
        if (code == null || code.isEmpty()) return null;
        return Arrays.stream(values())
                .filter(arch -> arch.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static Architecture findByName(String name) {
        if (name == null || name.isEmpty()) return null;
        return Arrays.stream(values())
                .filter(arch -> arch.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
