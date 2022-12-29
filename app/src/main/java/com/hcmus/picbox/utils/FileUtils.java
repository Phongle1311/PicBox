package com.hcmus.picbox.utils;

public class FileUtils {
    private static final String ReservedChars = "|\\?\\*<\":>\\+\\[]/'";

    public static boolean isValidFileName(String fileName) {
        return !fileName.matches(".*[" + ReservedChars + "].*");
    }
}
