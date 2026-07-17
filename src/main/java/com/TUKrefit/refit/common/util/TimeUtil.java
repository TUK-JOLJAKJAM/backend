package com.TUKrefit.refit.common.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class TimeUtil {
    private static final long MIN_REASONABLE_EPOCH_MS = 946_684_800_000L;
    private static final long MAX_REASONABLE_EPOCH_MS = 4_102_444_800_000L;
    private static final DateTimeFormatter LEGACY_UNITY_TIME = new DateTimeFormatterBuilder()
            .appendPattern("yyyyMMddHHmmss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 2, 2, false)
            .toFormatter();

    private TimeUtil() {}
    public static long nowMs() {
        return System.currentTimeMillis();
    }

    /**
     * Unity 구버전의 yyyyMMddHHmmssff 숫자와 v2 epoch milliseconds를 모두 수용합니다.
     */
    public static long normalizeGameTimestamp(long value) {
        if (value >= MIN_REASONABLE_EPOCH_MS && value <= MAX_REASONABLE_EPOCH_MS) return value;
        String raw = Long.toString(value);
        if (raw.length() != 16) return value;
        try {
            LocalDateTime parsed = LocalDateTime.parse(raw, LEGACY_UNITY_TIME);
            return parsed.toInstant(ZoneOffset.UTC).toEpochMilli();
        } catch (RuntimeException ignored) {
            return value;
        }
    }

    public static boolean isReasonableEpochMs(long value) {
        return value >= MIN_REASONABLE_EPOCH_MS && value <= MAX_REASONABLE_EPOCH_MS;
    }
}
