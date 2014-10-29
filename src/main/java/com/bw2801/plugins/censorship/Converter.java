package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.Action;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Converter {

    public static JsonObject convert(FileConfiguration config) {
        ConfigurationSection sec = config.getConfigurationSection("config.censorship");
        if (sec == null) return null;

        JsonObject root = new JsonObject();
        JsonArray words = new JsonArray();

        for (String word : sec.getKeys(false)) {
            String replace = config.getString("config.censorship." + word + ".replace-with", "***");
            String action = config.getString("config.censorship." + word + ".action", "none");
            int damage = config.getInt("config.censorship." + word + ".damage", 0);
            int points = config.getInt("config.censorship." + word + ".penalty-points", 0);
            List<String> commands = config.getStringList("config.censorship." + word + ".commands");
            List<String> exceptions = config.getStringList("config.censorship." + word + ".exceptions");

            if (commands == null) commands = new ArrayList<>();
            if (exceptions == null) exceptions = new ArrayList<>();

            try {
                Action.valueOf(action.toUpperCase());
            } catch (Exception ex) {
                action = "none";
            }

            JsonObject wordObj = new JsonObject();

            wordObj.add("word", new JsonPrimitive(word));
            wordObj.add("replace_with", new JsonPrimitive(replace));
            wordObj.add("action", new JsonPrimitive(action));
            wordObj.add("method", new JsonPrimitive("default"));

            wordObj.add("damage", new JsonPrimitive(damage));
            wordObj.add("penalty_points", new JsonPrimitive(points));

            JsonArray excs = new JsonArray();
            for (String exception : exceptions) {
                excs.add(new JsonPrimitive(exception));
            }
            wordObj.add("exceptions", excs);

            JsonArray cmnds = new JsonArray();
            for (String command : commands) {
                excs.add(new JsonPrimitive(command));
            }
            wordObj.add("commands", cmnds);

            words.add(wordObj);
        }

        root.add("words", words);
        return root;
    }
}
