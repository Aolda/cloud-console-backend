package com.acc.local.external.dto.google;

import java.util.List;

public record GoogleFormRequest(
        String timestamp,
        String name,
        String email,
        String department,
        Integer studentId,
        String projectName,
        String projectId,
        String projectPurpose,
        Integer cpuCores,
        Integer memorySize,
        Integer volumeSize
) {
    public static GoogleFormRequest from(List<Object> row) {
        return new GoogleFormRequest(
                String.valueOf(row.get(0)),
                String.valueOf(row.get(1)),
                String.valueOf(row.get(2)),
                String.valueOf(row.get(3)),
                parseInteger(row.get(4)),
                String.valueOf(row.get(5)),
                String.valueOf(row.get(6)),
                String.valueOf(row.get(7)),
                parseInteger(row.get(8)),
                parseInteger(row.get(9)),
                parseInteger(row.get(10))
        );
    }

    private static Integer parseInteger(Object value) {
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
