package com.agonyforge.core.controller.interpret.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuotingSplitter {
    private static final Pattern TOKENIZER = Pattern.compile("\"([^\"]*)\"?|'([^']*)'?|(\\S+)");

    private QuotingSplitter() {
        // this method intentionally left blank
    }

    public static String[] split(String in) {
        Matcher matcher = TOKENIZER.matcher(in);
        List<String> result = new ArrayList<>();

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    result.add(matcher.group(i));
                }
            }
        }

        return result.toArray(new String[0]);
    }
}
