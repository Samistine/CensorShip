package com.bw2801.plugins.censorship.replace;

import java.util.regex.Pattern;

public class AlternativeReplaceUtil implements ReplaceUtil {

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

        // If the string with the replaces words is one word long or there are
        // as many words as in the source string it will return the temporary
        // string, otherwise, it will run through another regex.
        if (wordCount == 1 || wordCount == source.split("\\s").length) {
            return temp;
        } else {
            // - There has to be a space in front of the word
            // - Ignore the same character mutliple times in a row
            // - Ignore any non-alphabetic characters
            // - Ignore any digits and whitespaces between characters
            // - Any amount of spaces at the end
            sb = new StringBuilder(4 * length - 3);
            sb.append("\\s+");

            for (int i = 0; i < length - 1; i++) {
                sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
            }
            sb.append("([\\W\\d]*\\s)");
            sb.append(search.charAt(length - 1));
        }

        String replace = source;

        if (wordCount <= 2) {
            replace = " " + source;
        }

        String result = replace.replaceAll("(?i)" + sb.toString(), search).trim();
        return result;
    }
}
