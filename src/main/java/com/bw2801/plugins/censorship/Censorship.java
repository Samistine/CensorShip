package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.ReplaceActionManager;
import com.bw2801.plugins.censorship.replace.AlternativeReplaceUtil;
import com.bw2801.plugins.censorship.replace.CompactReplaceUtil;
import com.bw2801.plugins.censorship.replace.DefaultReplaceUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Censorship extends JavaPlugin implements Listener {

    @Override
    public void onDisable() {
        print("Info", "Plugin enabled!");
    }

    @Override
    public void onEnable() {
        initReplaceUtils();
        loadWords();

        print("Info", "Plugin disabled!");
    }

    private void initReplaceUtils() {
        CensorUtil.addReplaceUtil("default", new DefaultReplaceUtil());
        CensorUtil.addReplaceUtil("alternative", new AlternativeReplaceUtil());
        CensorUtil.addReplaceUtil("compact", new CompactReplaceUtil());
        Object[] utils = CensorUtil.getReplaceUtils();

        print("Info", "Availiable replace methods (" + utils.length + "): " + Arrays.toString(utils));
    }

    private void loadWords() {
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

            try {
                try (FileWriter writer = new FileWriter(config)) {
                    writer.write("{\n"
                                 + "    \"word-files-used\": [\n"
                                 + "        \"words00\"\n"
                                 + "    ]\n"
                                 + "}");
                    writer.flush();
                }

                print("Info", "Successfully written to file.");
            } catch (IOException e) {
                print("Error", "Could not write to file...");
            }

            File s00 = new File("plugins/CensorShip/words00.json");
            if (!s00.exists()) {
                try {
                    s00.createNewFile();
                } catch (IOException ex) {
                }

                try {
                    try (FileWriter writer = new FileWriter(s00)) {
                        writer.write("{\n"
                                     + "	\"words\": [\n"
                                     + "		{\n"
                                     + "			\"word\": \"asshole\",\n"
                                     + "			\"replace_with\": \"a******\",\n"
                                     + "			\"action\": \"none\",\n"
                                     + "                        \"method\": \"default\",\n"
                                     + "\n"
                                     + "			\"exceptions\": [],\n"
                                     + "\n"
                                     + "			\"commands\": [\n"
                                     + "				\"tell <player> Don't say something like that!\"\n"
                                     + "			]\n"
                                     + "		}\n"
                                     + "	]\n"
                                     + "}");
                        writer.flush();
                    }
                } catch (IOException e) {
                }
            }
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(config));

            JSONArray words = (JSONArray) jsonObj.get("word-files-used");
            for (Object obj : words) {
                String word = (String) obj;

                print("Info", "Trying to read file \"" + word + ".json\"...");
                try {
                    ReplaceActionManager.loadActions(new File("plugins/CensorShip/" + word + ".json").getPath());
                } catch (Exception ex) {
                    print("Error", "Could not read file...");
                }
            }
        } catch (IOException | ParseException ex) {
        }

        print("Info", "Done parsing.");

        Config.init(this);
        Config.load();

        getCommand("censor").setExecutor(new CSCommandListener(this));
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
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

    public void startMuteSchedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (PlayerHandler.isMuted(player.getName())) {
                        PlayerHandler.mutePlayer(player.getName(), PlayerHandler.getMuteTime(player.getName()));

                        if (!PlayerHandler.isMuted(player.getName())) {
                            if (Config.getMessage("messages.unmuted").length() == 0) {
                                player.sendMessage(Config.getMessage("messages.unmuted"));
                            }
                            if (Config.getMessage("messages.unmuted-public").length() == 0) {
                                Bukkit.broadcastMessage(Config.getMessage("messages.unmuted-public"));
                            }
                        }
                    }
                }
            }
        }, 20l, 20l);
    }

    public static void print(String tag, Object... objects) {
        String string = "[CensorShip] " + tag + ":";

        for (Object obj : objects) {
            string += obj.toString();
        }

        System.out.println(string);
    }
}
