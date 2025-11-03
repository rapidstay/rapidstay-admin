package com.rapidstay.xap.admin.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {}

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMAT);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMAT);
    }

    public static LocalDate parseDate(String dateStr) {
        return (dateStr == null || dateStr.isEmpty()) ? null : LocalDate.parse(dateStr, DATE_FORMAT);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return (dateTimeStr == null || dateTimeStr.isEmpty()) ? null : LocalDateTime.parse(dateTimeStr, DATETIME_FORMAT);
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
