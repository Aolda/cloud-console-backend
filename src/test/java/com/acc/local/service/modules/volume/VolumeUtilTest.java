package com.acc.local.service.modules.volume;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VolumeUtilTest {

    private final VolumeUtil volumeUtil = new VolumeUtil();

    @Test
    @DisplayName("유효한 UUID 형식의 볼륨 ID를 검증할 수 있다")
    void givenValidUUID_whenValidateVolumeId_thenReturnTrue() {
        // given
        String validVolumeId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

        // when
        boolean result = volumeUtil.validateVolumeId(validVolumeId);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "invalid-id",
            "12345",
            "not-a-uuid",
            "a1b2c3d4e5f678abcdef1234567890",  // 하이픈 없음
            "a1b2c3d4-e5f6-7890-abcd-ef1234567890-extra"  // 너무 긺
    })
    @DisplayName("유효하지 않은 볼륨 ID를 검증하면 false를 반환한다")
    void givenInvalidVolumeId_whenValidateVolumeId_thenReturnFalse(String invalidVolumeId) {
        // when
        boolean result = volumeUtil.validateVolumeId(invalidVolumeId);

        // then
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "validName",
            "valid-name",
            "valid_name",
            "ValidName123",
            "a"
    })
    @DisplayName("유효한 볼륨 이름을 검증할 수 있다")
    void givenValidVolumeName_whenValidateVolumeName_thenReturnTrue(String validName) {
        // when
        boolean result = volumeUtil.validateVolumeName(validName);

        // then
        assertTrue(result);
    }

    // Note: 중문 검증은 정규식 설정에 따라 지원 여부가 결정됩니다.
    // 현재 정규식은 영문만 지원하도록 되어 있어 중문 테스트는 제외했습니다.

    @ParameterizedTest
    @ValueSource(strings = {
            "!invalidName",  // 특수문자로 시작
            "1invalidName",  // 숫자로 시작
            "-invalidName",  // 하이픈으로 시작
            "invalid name with spaces",  // 공백 포함
            "invalid@name"  // 허용되지 않는 특수문자
    })
    @DisplayName("유효하지 않은 볼륨 이름을 검증하면 false를 반환한다")
    void givenInvalidVolumeName_whenValidateVolumeName_thenReturnFalse(String invalidName) {
        // when
        boolean result = volumeUtil.validateVolumeName(invalidName);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("볼륨 이름이 null이거나 빈 문자열이면 true를 반환한다 (선택사항)")
    void givenNullOrEmptyVolumeName_whenValidateVolumeName_thenReturnTrue() {
        // when & then
        assertTrue(volumeUtil.validateVolumeName(null));
        assertTrue(volumeUtil.validateVolumeName(""));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000})
    @DisplayName("양수 볼륨 크기를 검증할 수 있다")
    void givenPositiveSize_whenValidateVolumeSize_thenReturnTrue(int size) {
        // when
        boolean result = volumeUtil.validateVolumeSize(size);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, -100})
    @DisplayName("0 이하의 볼륨 크기를 검증하면 false를 반환한다")
    void givenNonPositiveSize_whenValidateVolumeSize_thenReturnFalse(int size) {
        // when
        boolean result = volumeUtil.validateVolumeSize(size);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("null 볼륨 크기를 검증하면 false를 반환한다")
    void givenNullSize_whenValidateVolumeSize_thenReturnFalse() {
        // when
        boolean result = volumeUtil.validateVolumeSize(null);

        // then
        assertFalse(result);
    }
}