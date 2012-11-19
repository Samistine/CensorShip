package com.bw2801.plugins.censorship;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.bukkit.configuration.ConfigurationSection;

public class CensorShipAPI {

    /**
     * This method replaces all forbidden words in given string
     * @param string
     * string that should be checked.
     */
    public static String replace(String string) {        
        String result = string;
        List<String> actions = new ArrayList<String>();
        ConfigurationSection sec = Censorship.getConfiguration().getConfigurationSection("config.censorship");
        for (String search : sec.getKeys(false)) {
            result = Censorship.replace(result, search);
        }

        StringTokenizer st = new StringTokenizer(result);
        String string2 = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            for (String key : sec.getKeys(false)) {
                boolean contains = false;
                for (String item : Censorship.getConfiguration().getStringList("config.censorship." + key.toLowerCase() + ".exceptions")) {
                    if (token.toLowerCase().contains(item.toLowerCase())) {
                        contains = true;
                    }
                }
                if (!contains) {
                    if (token.toLowerCase().contains(key.toLowerCase())) {
                        String replaceWith = "";
                        for (int i = 0; i < key.length(); i++) {
                            replaceWith += "*";
                        }

                        if (Censorship.getConfiguration().getString("config.censorship." + key.toLowerCase() + ".replace-with") != null) {
                            replaceWith = Censorship.getConfiguration().getString("config.censorship." + key.toLowerCase() + ".replace-with");
                        }

                        List<Integer> positions = new ArrayList<Integer>();

                        for (int i = 0; i < token.length(); i++) {
                            if (Character.isUpperCase(token.charAt(i))) {
                                positions.add(i);
                            }
                        }

                        if (token.toLowerCase().contains(key.toLowerCase())) {
                            token = token.toLowerCase().replaceAll(key.toLowerCase(), replaceWith.toLowerCase());
                        }

                        for (int i : positions) {
                            if (i < token.length()) {
                                Character.toUpperCase(token.charAt(i));
                            }
                        }
                    }
                }
            }
            string2 = string2 + token + " ";
            
        }
        return string2;
    }
}