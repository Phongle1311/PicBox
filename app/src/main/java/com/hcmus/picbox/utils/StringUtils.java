package com.hcmus.picbox.utils;

public class StringUtils {

    public static String truncate(String string, int maxLength) {
        if (maxLength <= 0) return "";
        if (string.length() > maxLength) return string.substring(0, maxLength) + "...";
        return string;
    }
}
