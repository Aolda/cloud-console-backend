package com.acc.global.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class PaginationUtils {

    public static final int DEFAULT_LIMIT = 10;

    private PaginationUtils() {
    }

    public static PageRequest normalize(PageRequest request) {
        return normalize(request, true);
    }

    public static PageRequest normalize(PageRequest request, boolean allowFetchAll) {
        PageRequest normalized = new PageRequest();
        normalized.setMarker(normalizeMarker(request == null ? null : request.getMarker()));
        normalized.setDirection(
                request == null || request.getDirection() == null
                        ? PageRequest.Direction.next
                        : request.getDirection()
        );

        Integer requestedLimit = request == null ? null : request.getLimit();
        if (requestedLimit == null || requestedLimit < 0 || (!allowFetchAll && requestedLimit == 0)) {
            normalized.setLimit(DEFAULT_LIMIT);
        } else {
            normalized.setLimit(requestedLimit);
        }

        return normalized;
    }

    public static String normalizeMarker(String marker) {
        if (marker == null || marker.isBlank()) {
            return null;
        }
        return marker;
    }

    public static boolean isFetchAll(PageRequest request) {
        return normalize(request).getLimit() == 0;
    }

    public static boolean isPrevious(PageRequest request) {
        return normalize(request).getDirection() == PageRequest.Direction.prev;
    }

    public static int decodeOffsetMarker(String marker) {
        String normalizedMarker = normalizeMarker(marker);
        if (normalizedMarker == null) {
            return 0;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(normalizedMarker), StandardCharsets.UTF_8);
            return Math.max(Integer.parseInt(decoded), 0);
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public static String encodeOffsetMarker(int offset) {
        return Base64.getEncoder()
                .encodeToString(String.valueOf(Math.max(offset, 0)).getBytes(StandardCharsets.UTF_8));
    }

    public static String encodePositiveOffsetMarker(int offset) {
        if (offset <= 0) {
            return null;
        }
        return encodeOffsetMarker(offset);
    }
}
