package com.TUKrefit.refit.common.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

    @Test
    void preservesEpochMilliseconds() {
        long epochMs = 1_784_263_867_950L;
        assertEquals(epochMs, TimeUtil.normalizeGameTimestamp(epochMs));
        assertTrue(TimeUtil.isReasonableEpochMs(epochMs));
    }

    @Test
    void convertsLegacyUnityTimestampToEpochMilliseconds() {
        long legacy = 20_260_717_053_107_95L;
        long expected = Instant.parse("2026-07-17T05:31:07.950Z").toEpochMilli();
        assertEquals(expected, TimeUtil.normalizeGameTimestamp(legacy));
    }

    @Test
    void leavesMalformedTimestampDetectablyInvalid() {
        long malformed = 1_073_741_824L;
        long normalized = TimeUtil.normalizeGameTimestamp(malformed);
        assertEquals(malformed, normalized);
        assertFalse(TimeUtil.isReasonableEpochMs(normalized));
    }
}
