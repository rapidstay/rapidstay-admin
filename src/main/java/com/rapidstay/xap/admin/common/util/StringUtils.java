package com.rapidstay.xap.admin.common.util;

public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String removeWhitespace(String str) {
        return str == null ? null : str.replaceAll("\\s+", "");
    }
}
