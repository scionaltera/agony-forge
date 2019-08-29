package com.agonyforge.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatUtil {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^.+?(\\s+).+$");

    private FormatUtil() {
        // this method intentionally left blank
    }

    public static String removeFirstWord(String in) {
        Matcher matcher = WHITESPACE_PATTERN.matcher(in);

        if (matcher.matches()) {
            return in.substring(matcher.start(1)).trim();
        }

        return "";
    }
}
