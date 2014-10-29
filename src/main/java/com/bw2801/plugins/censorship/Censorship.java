package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.ReplaceActionManager;
import com.bw2801.plugins.censorship.commands.CSCommandListener;
import com.bw2801.plugins.censorship.replace.AlternativeReplaceUtil;
import com.bw2801.plugins.censorship.replace.CompactReplaceUtil;
import com.bw2801.plugins.censorship.replace.DefaultReplaceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Censorship extends JavaPlugin implements Listener {

    private final Set<BukkitTask> tasks = new HashSet<>();

    @Override
    public void onDisable() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        Config.save();
        ReplaceActionManager.saveActions();

        print("Info", "Disabled!");
    }

    @Override
    public void onEnable() {
        Config.init(this);
        Config.load();

        initReplaceUtils();
        loadWords();
        startSchedules();

        getCommand("censor").setExecutor(new CSCommandListener());
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        print("Info", "Enabled!");
    }

    public static void reload() {
        Config.load();
        loadWords();
    }

    private static void initReplaceUtils() {
        CensorUtil.addReplaceUtil("default", new DefaultReplaceUtil());
        CensorUtil.addReplaceUtil("alternative", new AlternativeReplaceUtil());
        CensorUtil.addReplaceUtil("compact", new CompactReplaceUtil());
    }

    private static void loadWords() {
        print("Info", "Loading censored words.");

        File config = new File("plugins/CensorShip/words-config.json");
        if (!config.exists()) {
            print("Warning", "Could not find \"words-config.json\".");
            print("Info", "Trying to create file...");
            try {
                config.createNewFile();
                print("Info", "File successfully created.");
            } catch (IOException ex) {
                print("Error", "File could not be created.");
            }
            print("Info", "Trying to write to file \"words-config.json\"...");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject root = new JsonObject();
            JsonArray files = new JsonArray();
            files.add(new JsonPrimitive("words"));
            root.add("command_created_words", new JsonPrimitive("words"));
            root.add("auto_save_interval", new JsonPrimitive(300));
            root.add("word_files_used", files);

            try {
                FileOutputStream stream = new FileOutputStream(config);
                stream.write(gson.toJson(root).getBytes());
                print("Info", "Successfully written to file.");
            } catch (IOException ex) {
                print("Error", "Could not write to file...");
            }

            File d00 = new File("plugins/CensorShip/words/");
            File s00 = new File("plugins/CensorShip/words/words.json");
            if (!s00.exists()) {
                try {
                    d00.mkdirs();
                    s00.createNewFile();
                } catch (IOException ex) {
                }

                JsonObject r = new JsonObject();
                JsonArray words = new JsonArray();
                JsonObject word = new JsonObject();

                word.add("word", new JsonPrimitive("example"));
                word.add("replace_with", new JsonPrimitive("e******"));
                word.add("action", new JsonPrimitive("none"));
                word.add("method", new JsonPrimitive("default"));
                word.add("damage", new JsonPrimitive(0));
                word.add("penalty_points", new JsonPrimitive(0));
                word.add("exceptions", new JsonArray());
                word.add("commands", new JsonArray());

                words.add(word);
                r.add("words", words);

                try {
                    FileOutputStream stream = new FileOutputStream(s00);
                    stream.write(gson.toJson(r).getBytes());
                } catch (IOException ex) {
                    print("Error", "Could not write to file...");
                }
            }
        }

        JsonParser parser = new JsonParser();
        JsonElement element = null;
        try {
            element = parser.parse(new JsonReader(new FileReader(config)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Censorship.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (element != null && element.isJsonObject()) {
            JsonObject root = (JsonObject) element;
            JsonArray files = (JsonArray) root.get("word_files_used");

            for (JsonElement file : files) {
                String word = file.getAsString();

                print("Info", "Trying to read file \"" + word + ".json\"...");
                try {
                    ReplaceActionManager.loadActions(word + ".json");
                } catch (Exception ex) {
                    print("Error", "Could not read file...");
                }
            }

            Config.setWordSaveFile(root.get("command_created_words").getAsString() + ".json");
            Config.setAutoSaveWordsInteval(root.get("auto_save_interval").getAsInt());
        } else {
            print("Error", "Could not read \"words-config.json\" file...");
        }

        print("Info", "Done parsing.");
    }

    private FileConfiguration playerConfig = null;
    private File playerFile = null;

    public void reloadPlayerConfig() {
        if (this.playerFile == null) {
            this.playerFile = new File(getDataFolder(), "players.yml");
        }
        this.playerConfig = YamlConfiguration.loadConfiguration(this.playerFile);

        InputStream defConfigStream = getResource("players.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.playerConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getPlayerConfig() {
        if (this.playerConfig == null) {
            reloadPlayerConfig();
        }
        return this.playerConfig;
    }

    public void savePlayerConfig() {
        if ((this.playerConfig == null) || (this.playerFile == null)) {
            return;
        }
        try {
            this.playerConfig.save(this.playerFile);

        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + this.playerFile, ex);
        }
    }
    private FileConfiguration messageConfig = null;
    private File messageFile = null;

    public void reloadMessageConfig() {
        if (this.messageFile == null) {
            this.messageFile = new File(getDataFolder(), "messages.yml");
        }
        this.messageConfig = YamlConfiguration.loadConfiguration(this.messageFile);

        InputStream defConfigStream = getResource("messages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.messageConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getMessageConfig() {
        if (this.messageFile == null) {
            reloadMessageConfig();
        }
        return this.messageConfig;
    }

    public void saveMessageConfig() {
        if ((this.messageConfig == null) || (this.messageFile == null)) {
            return;
        }
        try {
            this.messageConfig.save(this.messageFile);

        } catch (IOException ex) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + this.messageFile, ex);
        }
    }

    private void startSchedules() {
        tasks.add(Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

            @Override
            public void run() {
                for (String player : PlayerHandler.getTempBannedPlayers()) {
                    PlayerHandler.decreaseTempBanTime(player, 1);
                }

                for (Player player : getServer().getOnlinePlayers()) {
                    if (PlayerHandler.isMuted(player.getName())) {
                        PlayerHandler.mutePlayer(player.getName(), PlayerHandler.getMuteTime(player.getName()) - 1);

                        if (!PlayerHandler.isMuted(player.getName())) {
                            if (Config.getMessage("unmuted").length() != 0) {
                                Censorship.sendMessage(player, Config.getMessage("unmuted"));
                            }

                            if (Config.getMessage("unmuted-public").length() != 0) {
                                Bukkit.broadcastMessage(Config.getMessage("unmuted-public").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                            }
                        }
                    }
                }
            }
        }, 20l, 20l));

        long autoSaveInterval = 20l * Config.getAutoSaveWordsInteval();
        tasks.add(Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

            @Override
            public void run() {
                ReplaceActionManager.saveActions();
            }
        }, autoSaveInterval, autoSaveInterval));
    }

    public static void print(String tag, Object... objects) {
        String string = "[CensorShip] " + tag + ":";

        for (Object obj : objects) {
            string += " " + obj.toString();
        }

        System.out.println(string);
    }

    // Method for debugging what messages are sent to the players
    public static void sendMessage(Player player, Object... objects) {
        String string = "";

        for (Object obj : objects) {
            string += " " + obj.toString();
        }

        player.sendMessage(string.trim());
    }

    public static boolean hasPermission(CommandSender cs, String permission) {
        if (!cs.hasPermission(permission)) {
            cs.sendMessage(ChatColor.RED + Config.getMessage("no-permission"));
            return false;
        }
        return true;
    }
}
