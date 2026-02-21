package com.blackrock.challenge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public final class DatesUtil {

    public static final String FMT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FMT);
    private static final DateTimeFormatter FMT_OPTIONAL_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Optional<LocalDateTime> parseTimestamp(String s) {
        if (s == null || s.isBlank()) {
            return Optional.empty();
        }
        String trimmed = s.trim();
        try {
            if (trimmed.length() == 16) {
                return Optional.of(LocalDateTime.parse(trimmed + ":00", FORMATTER));
            }
            if (trimmed.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return Optional.of(LocalDateTime.parse(trimmed, FORMATTER));
            }
            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static String formatTimestamp(LocalDateTime date) {
        return date.format(FORMATTER);
    }

    public static boolean inRangeInclusive(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
