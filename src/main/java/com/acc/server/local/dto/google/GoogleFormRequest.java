package com.acc.server.local.dto.google;

public record GoogleFormRequest (
        String timestamp,
        String name,
        String email,
        String department,
        String studentId
) {}
