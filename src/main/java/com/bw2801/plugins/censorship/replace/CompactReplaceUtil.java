package com.bw2801.plugins.censorship.replace;

import java.util.regex.Pattern;

public class CompactReplaceUtil implements ReplaceUtil {

    @Override
    public String replace(String source, String search) {
        int length = search.length();
        if (length < 2) {
            return source;
        }

        StringBuilder sb = new StringBuilder(4 * length - 3);
        sb.append("\\s");
        for (int i = 0; i < length - 1; i++) {
            sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
        }
        sb.append("([\\W\\d\\s]*)+");
        sb.append(search.charAt(length - 1));

        String replace = source;
        String result = replace.replaceAll("(?i)" + sb.toString(), search).trim();
        return result;
    }
}
