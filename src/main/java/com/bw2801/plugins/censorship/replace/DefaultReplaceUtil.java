package com.bw2801.plugins.censorship.replace;

import java.util.regex.Pattern;

public class DefaultReplaceUtil implements ReplaceUtil {

    @Override
    public String replace(String source, String search) {
        int length = search.length();
        if (length < 2) {
            return source;
        }

        // - Ignore the same character mutliple times in a row
        // - Ignore any non-alphabetic characters
        // - Ignore any digits and whitespaces between characters
        StringBuilder sb = new StringBuilder(4 * length - 3);
        for (int i = 0; i < length - 1; i++) {
            sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
        }
        sb.append("([\\W\\d\\s]*)+");
        sb.append(search.charAt(length - 1));

        String temp = source.replaceAll("(?i)" + sb.toString(), search).trim();
        int wordCount = temp.split("\\s").length;

        String replace = source;

        if (wordCount <= 2) {
            replace = " " + source;
        }

        String result = replace.replaceAll("(?i)" + sb.toString(), " " + search).trim();
        return result;
    }
}
