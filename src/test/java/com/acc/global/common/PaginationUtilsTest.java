package com.acc.global.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaginationUtilsTest {

    @Test
    @DisplayName("PageRequest가 null이면 기본 페이지 요청으로 정규화한다.")
    void givenNullPageRequest_whenNormalize_thenUseDefaults() {
        PageRequest normalized = PaginationUtils.normalize(null);

        assertNull(normalized.getMarker());
        assertEquals(PageRequest.Direction.next, normalized.getDirection());
        assertEquals(10, normalized.getLimit());
    }

    @Test
    @DisplayName("blank marker와 음수 limit은 안전한 값으로 보정한다.")
    void givenInvalidValues_whenNormalize_thenUseSafeValues() {
        PageRequest request = new PageRequest();
        request.setMarker(" ");
        request.setDirection(null);
        request.setLimit(-1);

        PageRequest normalized = PaginationUtils.normalize(request);

        assertNull(normalized.getMarker());
        assertEquals(PageRequest.Direction.next, normalized.getDirection());
        assertEquals(10, normalized.getLimit());
    }

    @Test
    @DisplayName("offset marker를 Base64로 인코딩하고 디코딩한다.")
    void givenOffset_whenEncodeAndDecode_thenReturnOriginalOffset() {
        String marker = PaginationUtils.encodeOffsetMarker(20);

        assertEquals("MjA=", marker);
        assertEquals(20, PaginationUtils.decodeOffsetMarker(marker));
    }

    @Test
    @DisplayName("잘못된 offset marker는 첫 페이지로 처리한다.")
    void givenInvalidOffsetMarker_whenDecode_thenReturnZero() {
        assertEquals(0, PaginationUtils.decodeOffsetMarker("invalid-marker"));
    }
}
