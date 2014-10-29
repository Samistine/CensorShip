package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.Action;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Converter {

    private final Censorship plugin;
    private final String ymlFile;

    public Converter(Censorship plugin, String ymlFile) {
        this.plugin = plugin;
        this.ymlFile = ymlFile;
    }

    public JsonObject convert() {
        ConfigurationSection sec = getConvertConfig().getConfigurationSection("config.censorship");

        if (sec == null) {
            Censorship.print("Error", "Could not load \"" + ymlFile + ".yml\"");
            return null;
        }

        JsonObject root = new JsonObject();
        JsonArray words = new JsonArray();

        for (String word : sec.getKeys(false)) {
            String replace = getConvertConfig().getString("config.censorship." + word + ".replace-with", "***");
            String action = getConvertConfig().getString("config.censorship." + word + ".action", "none");
            int damage = getConvertConfig().getInt("config.censorship." + word + ".damage", 0);
            int points = getConvertConfig().getInt("config.censorship." + word + ".penalty-points", 0);
            List<String> commands = getConvertConfig().getStringList("config.censorship." + word + ".commands");
            List<String> exceptions = getConvertConfig().getStringList("config.censorship." + word + ".exceptions");

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

    private FileConfiguration convertConfig = null;
    private File configFile = null;

    public void reloadConvertConfig() {
        if (this.configFile == null) {
            this.configFile = new File(plugin.getDataFolder(), "words/convert/" + ymlFile + ".yml");
        }
        this.convertConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defConfigStream = plugin.getResource("words/convert/" + ymlFile + ".yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.convertConfig.setDefaults(defConfig);
        }
    }

    private FileConfiguration getConvertConfig() {
        if (this.convertConfig == null) {
            reloadConvertConfig();
        }
        return this.convertConfig;
    }
}
