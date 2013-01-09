package com.bw2801.plugins.censorship;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Bw2801
 * @date 2013-01-09
 * @version 2.2
 * 
 * @added customizable messages
 * @fixed check-commands censoring bug
 * @fixed sign text censoring bug
 */
public class Censorship extends JavaPlugin implements Listener {

    public static Censorship plugin;

    @Override
    public void onDisable() {
        System.out.println("[" + this.getName() + "] disabled!");
    }

    @Override
    public void onEnable() {
        loadConfig();
        loadPlayerConfig();
        loadMessages();
        plugin = this;

        getServer().getPluginManager().registerEvents(new CensorShipListener(this), this);
        System.out.println("[" + this.getName() + "] enabled!");
    }

    public static FileConfiguration getConfiguration() {
        Censorship cs = new Censorship();
        return cs.getConfig();
    }

    public static FileConfiguration getCustomConfiguration() {
        Censorship cs = new Censorship();
        return cs.getCustomConfig();
    }

    public String replace(String msg, Player player) {
        ConfigurationSection sec = getConfig().getConfigurationSection("config.censorship");
        String result = msg;
        List<String> actions = new ArrayList<String>();

        if (sec != null) {
            for (String search : sec.getKeys(false)) {
                result = replace(result, search);
            }
        }

        StringTokenizer st = new StringTokenizer(result);
        String string = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    boolean contains = false;
                    for (String item : getConfig().getStringList("config.censorship." + key.toLowerCase() + ".exceptions")) {
                        if (token.toLowerCase().contains(item.toLowerCase())) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        if (token.toLowerCase().contains(key.toLowerCase())) {
                            String action = "none";
                            List<String> cmd = new ArrayList<String>();
                            int pp = 1;
                            String replaceWith = "";
                            for (int i = 0; i < key.length(); i++) {
                                replaceWith += "*";
                            }
                            String rs = "";
                            int dmg = 0;

                            try {
                                if (getConfig().getString("config.censorship." + key.toLowerCase() + ".action") != null) {
                                    action = getConfig().getString("config.censorship." + key.toLowerCase() + ".action");
                                }
                                if (getConfig().getString("config.censorship." + key.toLowerCase() + ".commands") != null) {
                                    cmd = getConfig().getStringList("config.censorship." + key.toLowerCase() + ".commands");
                                }
                                if (getConfig().getString("config.censorship." + key.toLowerCase() + ".replace-with") != null) {
                                    replaceWith = getConfig().getString("config.censorship." + key.toLowerCase() + ".replace-with");
                                }
                                if (getConfig().getString("config.censorship." + key.toLowerCase() + ".mcbans.reason") != null) {
                                    rs = getConfig().getString("config.censorship." + key.toLowerCase() + ".mcbans.reason");
                                }
                                pp = getConfig().getInt("config.censorship." + key.toLowerCase() + ".penalty-points");
                                dmg = getConfig().getInt("config.censorship." + key.toLowerCase() + ".damage");
                            } catch (Exception e) {
                                System.out.println("[CensorShip] Could not pass word and used default settings.");
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

                            actions.add(action);
                            actions.add("pp:" + pp);
                            actions.add("dmg:" + dmg);
                            for (String command : cmd) {
                                actions.add("cmd:" + command.replaceAll("<player>", player.getName()).replaceAll("<target>", player.getName()).replaceAll("%player", player.getName()).replaceAll("%target", player.getName()).replaceAll("<reason>", rs).replaceAll("%reason", rs));
                            }
                        }
                    }
                }
            }
            string = string + token + " ";
        }

        if (string.endsWith(" ")) {
            string = string.substring(0, string.length() - 1);
        }
        
        if (!player.hasPermission("censor.bypass")) {

            int mp = getConfig().getInt("config.penalty-points.mute.points");
            int bp = getConfig().getInt("config.penalty-points.ban.points");

            for (String action : actions) {
                if (action.startsWith("cmd:") && getConfig().getBoolean("config.command.enabled")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("cmd:", ""));
                }
                if (getConfig().getBoolean("config.notify.enabled")) {
                    for (Player player2 : getServer().getOnlinePlayers()) {
                        if (player2.isOp()) {
                            player2.sendMessage(player.getName() + " used forbidden word(s).");
                        }
                    }
                }
                if (action.startsWith("dmg:") && getConfig().getBoolean("config.damage.enabled")) {
                    int hit = Integer.parseInt(action.replace("dmg:", ""));

                    if (player.getHealth() >= hit) {
                        player.setHealth(player.getHealth() - hit);
                    } else {
                        player.setHealth(0);
                    }

                    if (hit != 0) {
                        player.sendMessage(getMessageConfig().getString("messages.damaged"));
                    }
                }
                if (action.equals("ban")) {
                    player.setBanned(true);
                    player.kickPlayer(getMessageConfig().getString("messages.banned"));
                    getServer().broadcastMessage(getMessageConfig().getString("messages.banned-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                } else if (action.equals("kick")) {
                    player.kickPlayer(getMessageConfig().getString("messages.kicked"));
                    getServer().broadcastMessage(getMessageConfig().getString("messages.kicked-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                }
                if (action.startsWith("pp:")) {
                    int points = Integer.parseInt(action.replace("pp:", ""));
                    points += getCustomConfig().getInt("players.censorship." + player.getName() + ".penalty-points");

                    if (points >= bp) {
                        if (getConfig().getBoolean("config.penalty-points.ban.enabled")) {
                            player.kickPlayer(getMessageConfig().getString("messages.overused-banned"));
                            player.setBanned(true);
                            getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                            saveCustomConfig();
                        }
                    } else {
                        getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                        saveCustomConfig();
                    }

                    if (getConfig().getBoolean("config.penalty-points.mute.enabled")) {
                        if (points >= mp) {
                            getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                            setMute(player);
                            saveCustomConfig();
                        }
                    }
                }
            }
        }

        return string;
    }    

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 5) {
            if (args[0].equals("add")) {
                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String damage = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_p = "config.censorship." + word + ".penalty-points";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    getConfig().addDefault(path_w, replace);
                    getConfig().addDefault(path_p, 0);
                    getConfig().addDefault(path_a, action);
                    getConfig().addDefault(path_d, damage);

                    getConfig().set(path_w, replace);
                    getConfig().set(path_p, 0);
                    getConfig().set(path_a, action);
                    getConfig().set(path_d, damage);

                    saveConfig();
                    reloadConfig();

                    sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + "'.");

                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String damage = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_p = "config.censorship." + word + ".penalty-points";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    getConfig().addDefault(path_w, replace);
                    getConfig().addDefault(path_p, 0);
                    getConfig().addDefault(path_a, action);
                    getConfig().addDefault(path_d, damage);

                    getConfig().set(path_w, replace);
                    getConfig().set(path_p, 0);
                    getConfig().set(path_a, action);
                    getConfig().set(path_d, damage);

                    saveConfig();
                    reloadConfig();

                    sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + "'.");
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 4) {
            if (args[0].equals("add")) {
                if (args[1].equals("exception")) {
                    if (sender.hasPermission("censor.add")) {
                        String word = args[2];
                        String exec = args[3];
                        String path_w = "config.censorship." + word + ".exceptions";

                        List<String> list = getConfig().getStringList(path_w);
                        if (!list.contains(exec)) {
                            list.add(exec);
                        } else {
                            sender.sendMessage(ChatColor.RED + "This exceptions was already added.");
                            return true;
                        }

                        getConfig().addDefault(path_w, list);
                        getConfig().set(path_w, list);

                        saveConfig();
                        reloadConfig();

                        sender.sendMessage("Added Exception '" + ChatColor.GOLD + exec + ChatColor.WHITE + "' for the word '" + ChatColor.GOLD + word + ChatColor.WHITE + "'.");
                    } else {
                        sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                    }

                    return true;
                }

                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";
                    String path_p = "config.censorship." + word + ".penalty-points";

                    getConfig().addDefault(path_w, replace);
                    getConfig().addDefault(path_a, action);
                    getConfig().addDefault(path_p, 0);
                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);
                    getConfig().set(path_p, 0);

                    saveConfig();
                    reloadConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Added '" + word + "' - Replace with '" + replace + "' - Action: '" + action + "'.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";

                    getConfig().addDefault(path_w, replace);
                    getConfig().addDefault(path_a, action);
                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);

                    saveConfig();
                    reloadConfig();

                    sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");

                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 3) {
            if (args[1].equals("add")) {
                if (sender.hasPermission("censor.penalty-points.add")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = getCustomConfig().getInt("players.censorship." + target + ".pentalty-points");

                        int out = path_i.intValue() + points;

                        getConfig().set(path_p, out);
                        saveCustomConfig();

                        sender.sendMessage("Added '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points to '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[1].equals("remove")) {
                if (sender.hasPermission("censor.penalty-points.remove")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = getCustomConfig().getInt("players.censorship." + target + ".pentalty-points");

                        int out = path_i.intValue() - points;

                        getConfig().set(path_p, out);
                        saveCustomConfig();

                        sender.sendMessage("Removed '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points from '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 2) {
            if (args[0].equals("remove")) {
                if (sender.hasPermission("censor.remove")) {
                    String word = args[1];
                    String path_all = "config.censorship." + word;

                    getConfig().addDefault(path_all, null);
                    getConfig().set(path_all, null);

                    saveConfig();
                    reloadConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Removed '" + ChatColor.GOLD + word + ChatColor.WHITE + "'");
                    } else {
                        sender.sendMessage("Removed '" + word + "'");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (sender.hasPermission("censor.reload")) {
                    reloadConfig();
                    reloadCustomConfig();
                    reloadMessageConfig();
                    sender.sendMessage("CensorShip configurations reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("help")) {
                sender.sendMessage(ChatColor.AQUA + "=== CensorShip - Help ===");
                sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <action> [damage] " + ChatColor.GRAY + "- Add a word to the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception> " + ChatColor.GRAY + "- Add an exception to a word.");
                sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <action> [damage] " + ChatColor.GRAY + "- Changes a word in the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor remove <word> " + ChatColor.GRAY + "- Removes a word from the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points> " + ChatColor.GRAY + "- Adds penalty-points to a player.");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points> " + ChatColor.GRAY + "- Removes penalty-points of a player.");
                sender.sendMessage(ChatColor.GOLD + "/censor reload " + ChatColor.GRAY + "- Reloads the configuration file.");
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else {
            sender.sendMessage(ChatColor.AQUA + "=== CensorShip - Help ===");
            sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <action> [damage] " + ChatColor.GRAY + "- Add a word to the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception> " + ChatColor.GRAY + "- Add an exception to a word.");
            sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <action> [damage] " + ChatColor.GRAY + "- Changes a word in the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor remove <word> " + ChatColor.GRAY + "- Removes a word from the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points> " + ChatColor.GRAY + "- Adds penalty-points to a player.");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points> " + ChatColor.GRAY + "- Removes penalty-points of a player.");
            sender.sendMessage(ChatColor.GOLD + "/censor reload " + ChatColor.GRAY + "- Reloads the configuration file.");
        }
        return false;
    }

    public static String replace(String source, String search) {
        List<String> words = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(source);

        while (st.hasMoreTokens()) {
            words.add(st.nextToken());
        }

        int length = search.length();
        if (length < 2) {
            return source;
        }
        StringBuilder sb = new StringBuilder(4 * length - 3);
        for (int i = 0; i < length - 1; i++) {
            sb.append(search.charAt(i));
            sb.append("\\s*");
            sb.append("\\.*");
            sb.append("\\,*");
            sb.append("\\-*");
            sb.append("\\_*");
            sb.append("\\~*");
        }
        sb.append(search.charAt(length - 1));

        source = source.replaceAll("(?i)" + sb.toString(), search);
        return source;
    }

    private void loadPlayerConfig() {
        String players = "players.censorship";
        getCustomConfig().addDefault(players, null);

        getCustomConfig().options().copyDefaults(true);
        saveCustomConfig();
    }

    private void loadMessages() {
        FileConfiguration config = getMessageConfig();
        config.addDefault("messages.no-permission", "You don't have permissions to perform this command.");
        config.addDefault("messages.damaged", "You were damaged for using forbidden word(s).");
        config.addDefault("messages.banned", "You were banned for using forbidden word(s).");
        config.addDefault("messages.banned-public", "<player> was banned for using forbidden word(s).");
        config.addDefault("messages.kicked", "You were kicked for using forbidden word(s).");
        config.addDefault("messages.kicked-public", "<player> was kicked for using forbidden word(s).");
        config.addDefault("messages.overused-banned", "You were banned for overusing forbidden words.");
        config.addDefault("messages.muted", "You are muted! Nobody can hear you.");
        config.addDefault("messages.muted-public", "<player> is muted now for <time> minutes.");
        config.addDefault("messages.unmuted-public", "<player> is no longer muted.");
        config.options().copyDefaults(true);
        saveMessageConfig();
    }

    private void loadConfig() {
        String comment = "Thank you for downloading this plugin. In the next lines are tips to add/edit words in the config.\n"
                + "This is Version " + this.toString().replaceAll("CensorShip v", "") + "\n\n"
                + "The \"example-word-to-replace\" cannot be removed! Sorry for that :/\n"
                + "The first few configurations you can modify are \"damage\", \"command\" and \"penalty-points\".\n"
                + "If you enable damage, the players will get hurt for using forbidden words.\n"
                + "If you set the ban-option to true a player will get banned after getting given amount of penalty-points.\n"
                + "If you set the mute-option to true a player will get muted for given time after getting given amount of penalty-points.\n"
                + "If you set the command-option to true custom commands can be executed for using a forbidden word.\n\n"
                + "Next there is the possibillity to set the notify-option to enabled. This automatically notices all operators if a player uses forbidden words."
                + "Also you can use the autoupdate options. You are able to set checking true and you can set the automatically downloading of the newest version to true."
                + "To add a word you can use the command \"/censor add <word> <replace-with> <action> [damage]\" or you can type it here.\n"
                + "First of all you have to add the word that should be replaced. After that you choose a word or symbols to replace that word with.\n"
                + "Now you can add a custom command like \"ban <player> <reason>\"(without slash!).\n"
                + "If that command is a mcbans command, you can set the reason for banning.\n"
                + "After that command crap you can choose how many penalty-points someone will get for using this word.\n"
                + "Done? Ok. Now set the exceptions if you want.\n\n"
                + "== Example ==\n"
                + "config:\n"
                + "  censorship:\n"
                + "    god:\n"
                + "      replace-with: 'notch'\n"
                + "      action: 'none'\n"
                + "      commands:\n"
                + "      - 'say don't do this again <player>!'\n"
                + "      - 'any other command'\n"
                + "      - '...'\n"
                + "      mcbans:\n"
                + "        reason: '<player> used forbidden words in chat.'\n"
                + "      penalty-points: 0\n"
                + "      exceptions:\n"
                + "      - Godship\n"
                + "      - Godsons\n\n"
                + ""
                + "I added something new in 1.9.9! There you can set the \"check-commands\"-option to true. That means that the plugin will catch the PLAYER-commands you put in the list\n"
                + "and replaces the message in the plugin. To let this plugin know where it have to start to censor you have to put a <msg>-tag at the place where the message normaly\n"
                + "begins. You can do it like this:\n\n"
                + "config:\n"
                + "  ..."
                + "  check-commands:\n"
                + "    enabled: true\n"
                + "    list:\n"
                + "    - /msg <name> <msg>\n"
                + "    - /r <msg>\n"
                + "    - ...\n"
                + "\n"
                + "The <msg>-tag means that the plugin will check everything from there to the end of the input. The <name>-tag is just a placeholder and you can call it however you want.\n"
                + "If there was a command formatted like this: \"/cmdname <from> <to> <msg>\" you can change <from> and <to> to whatever you want like this:\n"
                + "\"/cmdname <blub> <blah> <msg>\".\n\n"
                + ""
                + "In version 2.0 I added the option to censore sign text. Just tick \"true\" in \"signs - enabled\".";

        getConfig().options().header(comment);

        String[] list = {"/msg <name> <msg>", "/r <msg>"};

        getConfig().addDefault("config.damage.enabled", false);
        getConfig().addDefault("config.notify.enabled", false);
        getConfig().addDefault("config.signs.enabled", true);
        getConfig().addDefault("config.tolowercase.enabled", false);
        getConfig().addDefault("config.tolowercase.percent", 50);
        getConfig().addDefault("config.penalty-points.ban.enabled", false);
        getConfig().addDefault("config.penalty-points.ban.points", 3);
        getConfig().addDefault("config.penalty-points.mute.enabled", false);
        getConfig().addDefault("config.penalty-points.mute.points", 3);
        getConfig().addDefault("config.penalty-points.mute.seconds", 300);
        getConfig().addDefault("config.command.enabled", false);
        getConfig().addDefault("config.check-commands.enabled", false);
        getConfig().addDefault("config.check-commands.list", list);
        getConfig().addDefault("config.censorship", "");
        getConfig().options().copyDefaults(true);

        saveConfig();
        reloadConfig();
    }

    public void setMute(final Player player) {
        int sec = getConfig().getInt("config.penalty-points.mute.seconds");
        long seconds = 20 * sec;
        double minuites = 20 * sec / 1200;

        getCustomConfig().set("players.censorship." + player.getName() + ".muted", false);
        getServer().broadcastMessage(getMessageConfig().getString("messages.muted-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE).replace("<time>", ChatColor.GOLD + "" + minuites + ChatColor.WHITE));
        saveConfig();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            public void run() {
                getCustomConfig().set("players.censorship." + player.getName() + ".muted", false);
                getServer().broadcastMessage(getMessageConfig().getString("messages.unmuted-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                saveCustomConfig();
            }
        }, seconds);
    }

    private FileConfiguration playerConfig = null;
    private File playerFile = null;

    public void reloadCustomConfig() {
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

    public FileConfiguration getCustomConfig() {
        if (this.playerConfig == null) {
            reloadCustomConfig();
        }
        return this.playerConfig;
    }

    public void saveCustomConfig() {
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
}