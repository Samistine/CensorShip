package de.bw2801.plugins.censorship;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Censorship extends JavaPlugin implements Listener {

    Map<Player, Integer> spam;
    private FileConfiguration playerConfig = null;
    private File playerFile = null;

    public void onDisable() {
        System.out.println(this + " disabled!");
    }

    public void onEnable() {
        loadConfig();
        loadPlayerConfig();

        getServer().getPluginManager().registerEvents(this, this);
        System.out.println(this + " enabled!");
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        boolean muted = getCustomConfig().getBoolean("players.censorship." + event.getPlayer().getName() + ".muted");

        if (muted) {
            event.getPlayer().sendMessage("You are muted! Nobody can hear you!");
            event.setCancelled(true);
        } else {
            ConfigurationSection sec = getConfig().getConfigurationSection("config.censorship");
            String result = msg;
            for (String search : sec.getKeys(false)) {
                result = replace(result, search);
            }
            
            StringTokenizer st = new StringTokenizer(result);
            String string = "";
            
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                for (String key : sec.getKeys(false)) {
                    boolean contains = false;
                    for (String item : getConfig().getStringList("config.censorship." + key + ".exceptions")) {
                        if (token.contains(item)) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        try {
                            String replaceWith = getConfig().getString("config.censorship." + key + ".replace-with");
                            token = token.replaceAll(key, replaceWith.toLowerCase());
                            System.out.println(result);

                            String action = getConfig().getString("config.censorship." + key + ".action");

                            boolean damage = getConfig().getBoolean("config.damage.enabled");
                            int damageValue = getConfig().getInt("config.censorship." + key + ".damage");
                            boolean command = getConfig().getBoolean("config.command.enabled");
                            boolean mute = getConfig().getBoolean("config.penalty-points.mute.enabled");
                            boolean point = getConfig().getBoolean("config.penalty-points.ban.enabled");
                            int points;
                            try {
                                points = getCustomConfig().getInt("players.censorship." + event.getPlayer().getName() + ".penalty-points");
                            } catch (Exception e) {
                                String path = "players.censorship." + event.getPlayer().getName() + ".penalty-points";
                                String path2 = "players.censorship." + event.getPlayer().getName() + ".muted";
                                getCustomConfig().addDefault(path, 0);
                                getCustomConfig().addDefault(path2, false);
                                saveCustomConfig();
                                points = getCustomConfig().getInt("players.censorship." + event.getPlayer().getName() + ".penalty-points");
                            }

                            int addpoints = getConfig().getInt("config.censorship." + key + ".penalty-points");
                            int mp = getConfig().getInt("config.penalty-points.mute.points");
                            int maxpoints = getConfig().getInt("config.penalty-points.ban.points");
                            String ban2message = "You were banned, becuase of overusing forbidden words.";
                            String kickmessage = "You were kicked. The word " + ChatColor.GOLD + key + " is not allowed on this server.";
                            String banmessage = "You were banned. The word " + ChatColor.GOLD + key + " is not allowed on this server.";

                            if (damage) {
                                event.getPlayer().setHealth(event.getPlayer().getHealth() - damageValue);
                                event.getPlayer().sendMessage("You were damaged for using forbidden word(s).");
                            }
                            if (command) {
                                String reason = getConfig().getString("config.censorship." + key + ".mcbans.reason");
                                String cmnd = getConfig().getString("config.censorship." + key + ".command");
                                cmnd = cmnd.replaceAll("<player>", event.getPlayer().getName()).replaceAll("<target>", event.getPlayer().getName()).replaceAll("%player", event.getPlayer().getName()).replaceAll("%target", event.getPlayer().getName()).replaceAll("<reason>", reason).replaceAll("%reason", reason);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmnd);
                            }

                            if (!event.getPlayer().hasPermission("censor.disallow")) {
                                if (point) {
                                    if (points >= maxpoints - 1) {
                                        points = maxpoints;

                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        saveCustomConfig();

                                        event.getPlayer().kickPlayer(ban2message);
                                        event.getPlayer().setBanned(true);
                                        getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was banned for overusing forbidden words.");
                                    } else if ("ban".equals(action)) {
                                        points += addpoints;
                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        saveCustomConfig();

                                        event.getPlayer().kickPlayer(banmessage);
                                        event.getPlayer().setBanned(true);
                                        getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was banned for using forbidden word(s).");
                                    } else if ("kick".equals(action)) {
                                        points += addpoints;
                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        saveCustomConfig();

                                        event.getPlayer().kickPlayer(kickmessage);
                                        getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was kicked for using forbidden word(s).");
                                    }
                                } else if (mute == true) {
                                    if (points >= mp - 1) {
                                        points = mp;
                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        setmute(event.getPlayer());
                                        saveCustomConfig();
                                    } else if ("ban".equals(action)) {
                                        points += addpoints;
                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        saveCustomConfig();

                                        event.getPlayer().kickPlayer(banmessage);
                                        event.getPlayer().setBanned(true);
                                        getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was banned for using forbidden word(s).");
                                    } else if ("kick".equals(action)) {
                                        points += addpoints;
                                        getCustomConfig().set("players.censorship." + event.getPlayer().getName() + ".penalty-points", Integer.valueOf(points));
                                        saveCustomConfig();

                                        event.getPlayer().kickPlayer(kickmessage);
                                        getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was kicked for using forbidden word(s).");
                                    }
                                } else if ("kick".equals(action)) {
                                    event.getPlayer().kickPlayer(kickmessage);
                                    getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was kicked for using forbidden word(s).");
                                } else if ("ban".equals(action)) {
                                    event.getPlayer().kickPlayer(banmessage);
                                    event.getPlayer().setBanned(true);
                                    getServer().broadcastMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " was banned for using forbidden word(s).");
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                string += token;
            }
            event.setMessage(string);
        }
    }

    public static String replace(final String source, final String search) {
        final int length = search.length();
        if (length < 2) {
            return source;
        }
        final StringBuilder sb = new StringBuilder(4 * length - 3);
        for (int i = 0; i < length - 1; i++) {
            sb.append(search.charAt(i));
            sb.append("\\s*");
        }
        sb.append(search.charAt(length - 1));
        return source.replaceAll(sb.toString(), search);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 5) {
            if (args[0].equals("add")) {
                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String damage = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);
                    getConfig().set(path_d, damage);
                    saveConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Added '" + word + "' - Replace with '" + replace + "' - Action: '" + action + "' - Damage: '" + damage + ".");
                    }

                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to add forbidden words.");
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String damage = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);
                    getConfig().set(path_d, damage);
                    saveConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Updated '" + word + "' - Replace with '" + replace + "' - Action: '" + action + "' - Damage: '" + damage + ".");
                    }

                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to update forbidden words.");
                }
            }
        } else if (args.length == 4) {
            if (args[0].equals("add")) {
                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";

                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);
                    saveConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Added '" + word + "' - Replace with '" + replace + "' - Action: '" + action + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to add forbidden words.");
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";

                    getConfig().set(path_w, replace);
                    getConfig().set(path_a, action);
                    saveConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Updated '" + word + "' - Replace with '" + replace + "' - Action: '" + action + ".");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to update forbidden words.");
                }
            }
        } else if (args.length == 3) {
            if (args[1].equals("add")) {
                if (sender.hasPermission("censor.penalty-points.add")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = Integer.valueOf(getCustomConfig().getInt("players.censorship." + target + ".pentalty-points"));

                        int out = path_i.intValue() + points;

                        getConfig().set(path_p, Integer.valueOf(out));
                        saveCustomConfig();

                        sender.sendMessage("Added '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points to '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to add penalty points for a player.");
                }
            } else if (args[1].equals("remove")) {
                if (sender.hasPermission("censor.penalty-points.remove")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = Integer.valueOf(getCustomConfig().getInt("players.censorship." + target + ".pentalty-points"));

                        int out = path_i.intValue() - points;

                        getConfig().set(path_p, Integer.valueOf(out));
                        saveCustomConfig();

                        sender.sendMessage("Removed '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points from '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to remove penalty points for a player.");
                }
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
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to remove forbidden words.");
                }
            }
        } else {
            if ((sender instanceof Player)) {
                sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <action> [damage]");
                sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <action> [damage]");
                sender.sendMessage(ChatColor.GOLD + "/censor remove <word>");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points>");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points>");
            } else {
                sender.sendMessage("/censor add <word> <replace-with> <action> [damage]");
                sender.sendMessage("/censor update <word> <replace-with> <action> [damage]");
                sender.sendMessage("/censor remove <word>");
                sender.sendMessage("/censor <player> add <penalty-points>");
                sender.sendMessage("/censor <player> remove <penalty-points>");
            }
        }
        return false;
    }

    private void loadPlayerConfig() {
        String players = "players.censorship";
        getCustomConfig().addDefault(players, null);

        getCustomConfig().options().copyDefaults(true);
        saveCustomConfig();
    }

    private void loadConfig() {
        String[] list = {"example-word-to-replace-exception"};
        
        getConfig().options().header("Version 1.9");
        getConfig().addDefault("config.damage.enabled", false);
        getConfig().addDefault("config.penalty-points.ban.enabled", true);
        getConfig().addDefault("config.penalty-points.ban.points", 3);
        getConfig().addDefault("config.penalty-points.mute.enabled", false);
        getConfig().addDefault("config.penalty-points.mute.points", 3);
        getConfig().addDefault("config.penalty-points.mute.seconds", 300);
        getConfig().addDefault("config.command.enabled", false);
        getConfig().addDefault("config.censorship.example-word-to-replace.replace-with", "***");
        getConfig().addDefault("config.censorship.example-word-to-replace.action", "ban");
        getConfig().addDefault("config.censorship.example-word-to-replace.command", "ban <player> <reason>");
        getConfig().addDefault("config.censorship.example-word-to-replace.mcbans.reason", "<player> used forbidden words in chat.");
        getConfig().addDefault("config.censorship.example-word-to-replace.penalty-points", "3");
        getConfig().addDefault("config.censorship.example-word-to-replace.exceptions", list);

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void setmute(final Player player) {
        int sec = getConfig().getInt("config.penalty-points.mute.seconds");
        long seconds = 20 * sec;
        double minuites = 20 * sec / 1200;

        getCustomConfig().set("players.censorship." + player.getName() + ".muted", Boolean.valueOf(true));
        getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " is now muted for " + ChatColor.GOLD + minuites + " minuites" + ChatColor.WHITE + ".");
        saveConfig();

        getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {

            public void run() {
                Censorship.this.getCustomConfig().set("players.censorship." + player.getName() + ".muted", Boolean.valueOf(false));
                Censorship.this.getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", Integer.valueOf(0));
                Censorship.this.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " is no longer muted.");
                Censorship.this.saveCustomConfig();
            }
        }, seconds);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean muted = getConfig().getBoolean("players.censorship." + event.getPlayer() + ".muted");

        if (muted == true) {
            setmute(event.getPlayer());
        }
    }

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
}