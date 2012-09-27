package com.bw2801.plugins.censorship;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

public class Censorship extends JavaPlugin
        implements Listener {

//_______________sss$$$$$s$ss
//___________s$$$ss_______s$$$$sss
//________s$$ss_________s$$$s__s$$sss
//______$$$s_________s$$ss___________sss
//____$$$__________s$$s_______$s_______ss
//___$$____________$s____s$________$_____ss
//__$$____________$$______________________ss
//_$$____ssss_____$___$s___ss__________$s__ss
//_$____$$$$s$$___$____________s$___________$s
//s$_____s$s$$$s__s$s_______________________ss
//$$______$$$s$s____$$________________$s_____s
//$$_____________ss__$$__s$__________________s
//s$______s______$$$$s_______________________s
//_$$_____s_ss$$s___$$__________$s_______s$$$s
//__$s______$s$$$$s_s$$$s____________s$$$s_$$
//___$s__________sss___s$$$$s$s$$s$$$$s___$$
//____$$s___________________sssss_______$$s
//_____s$$s___________________________$$$
//_______s$$$s_____________________s$$$
//__________s$$$$ss$s$s$$$sLGSw$$$$s
//_______________sssssssssssss
//___________________________
//_________________________$$______ss
//_________________________s$$s__s$$$
//__________________________$_$$$$e$
//_______________________sss$s_s__$$
//_____________________$$$$s______s$$$ss
//_______________________ss$$_____s$$$$$s
//_________________________s$_s$s_$
//_________________________$s$$s$$$s
//_____s$s____$$
//______$$$s$s$
//______$_$$s$$
//_s$$$ss____$ssss
//__s$$$$_____$$$$$
//_____s$_$$_$s______________________ss
//_____$$$ss$ss___s$ss______________sssss
//____s$_____$$____s$$s______$$sss$$sss$s_$$$
//___________________s$$s_s$$$$$ssss_ss$sss$s
//_____________________s$$$$$$$$s$$$$sssss$$s
//______________________s$$$$_s$s__s$s_ss$sss$ss
//_____________________s$s$s_$LG$$s_s$$$ssss$$$$$s
//________________________$s_$$sssss_s$$$sssss$ss
//________________________s_s$ss$$s_ss$s$$ss$$$s$$
//______________________s$$$$s_s$s__ss_sssss$ss$$$ss
//______________________$$Sws__s$______s$$$ssss$$ssss
//_____s$$$s______________ss__s$_______$$$$ssss$s$s
//__s$$$s_s$$$$$$$ss_________$$s__s$s____s$s_sss_$s
//_$$$$$$$$$$ss$sssss____sss$$s__$$s_____$$s_ss$ss
//s$s_s_sss____ss$$sssss$ss_______ss$$$s$$s$ss$$$s
//___________s$$sss$s_s$ssss______s$s$$s$s_sss$s
//______s$$$$s$s$$s_ss$$ss_______s$$$sss$$s$$s$ss
//_____$$$s$$s____s$sss$s________ss$$s_$sss$$sssss
//___s$$$$$s_______ss$s_ss__ssss$$$$ss$s$$$$s
//___$$sss___________$s___ss_____$ss$$$$$s$$s
//__s$$s______________s$$sss___s$s$ss$$s_ss_s$$s
//___s_________________s$$$s__s$ss$s$$s_______s$$s___________s
//________________________s$s__ss__s$___________s$$s_____ss$s$$s$$s
//_________________________s$s____________________s$$_s$$$$$ss$ss$ssss
//___________________________s$$s___________________$$ss$$$sssssss$$$s
//_____________________________s$$s$$s______sss__s__$$_____ss$ssssse$s
//_________________________________s$s$$$s__ss___s__s$___sss$$$ss_s$ss
//_________________________________$____s$s_________$s_s$$ssssss__s$$$s
//_________________________________ss____ss________s$__s$ssss__s$s$sss
//_________________________________ss____s$s______s$___ssss$$s_s$ss$_ss
//__________________________________$s__s$_s$s___s$___s$ss$$$s$s__sss_s
//__________________________________s$__$$__ss___$s_ss$$s_s$ss$$ss$__s
//__________________________________$$__ss___s$s_s$_ss$s$$sssssssssss$$s
//__________________________________s$__ss____ss__s$_ss$s$ssssss__ssss$s
//__________________________________s_s$s______ss_s$s$$ss_s$ss$$__$s___s
//_________________________________ss$$s______ss$_$ss$s$$$$sssss____s$s
//____________________________ss$sss$$s_____sss$$s_$ssss$ss_ssssss$sss$
//__________________________s$$$ssss$s___s$$$_s$$s_s$$$s__sssss$$$sss
//________________________s$$$$$$$$$s___s$$$$$$$s_sss_sss_s$ssss$ss$s
//_______________________s$s_$$sss____s$$ss$s$$s_s$$$s$ss$sss$$sss$$
//____________________________________ss__________ss___s$$s$____ss
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
                                getCustomConfig().addDefault(path, Integer.valueOf(0));
                                getCustomConfig().addDefault(path2, Boolean.valueOf(false));
                                saveCustomConfig();
                                points = getCustomConfig().getInt("players.censorship." + event.getPlayer().getName() + ".penalty-points");
                            }

                            int addpoints = getConfig().getInt("config.censorship." + key + ".penalty-points");
                            int mp = getConfig().getInt("config.penalty-points.mute.points");
                            int maxpoints = getConfig().getInt("config.penalty-points.ban.points");
                            String ban2message = "You were banned, becuase of overusing forbidden words.";
                            String kickmessage = "You were kicked. The word " + ChatColor.GOLD + key + ChatColor.WHITE + " is not allowed on this server.";
                            String banmessage = "You were banned. The word " + ChatColor.GOLD + key + ChatColor.WHITE + " is not allowed on this server.";

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
                string = string + token + " ";
            }
            event.setMessage(string);
        }
    }

    public static String replace(String source, String search) {
        int length = search.length();
        if (length < 2) {
            return source;
        }
        StringBuilder sb = new StringBuilder(4 * length - 3);
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

                    sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + ".");

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

                    sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Damage: '" + ChatColor.GOLD + damage + ChatColor.WHITE + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to update forbidden words.");
                }
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
                        sender.sendMessage(ChatColor.RED + "You don't have permissions to add exceptions.");
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
                    sender.sendMessage(ChatColor.RED + "You don't have permissions to add forbidden words.");
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
            sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <action> [damage]");
            sender.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception>");
            sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <action> [damage]");
            sender.sendMessage(ChatColor.GOLD + "/censor remove <word>");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points>");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points>");
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

        String comment = "Thank you for downloading this plugin. In the next lines are tips to add/edit words in the config.\n"
                + "This is Version " + this.toString().replaceAll("CensorShip v", "") + "\n\n"
                + "The \"example-word-to-replace\" cannot be removed! Sorry for that :/\n"
                + "The first few configurations you can modify are \"damage\", \"command\" and \"penalty-points\".\n"
                + "If you enable damage, the players will get hurt for using forbidden words.\n"
                + "If you set the ban-option to true a player will get banned after getting given amount of penalty-points.\n"
                + "If you set the mute-option to true a player will get muted for given time after getting given amount of penalty-points.\n"
                + "If you set the command-option to true custom commands can be executed for using a forbidden word.\n\n"
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
                + "      command: 'say don't do this again <player>!'\n"
                + "      mcbans:\n"
                + "        reason: '<player> used forbidden words in chat.'\n"
                + "      penalty-points: 0\n"
                + "      exception:\n"
                + "      - Godship\n"
                + "      - Godsons\n";
        
        getConfig().options().header(comment);
        
        getConfig().addDefault("config.damage.enabled", Boolean.valueOf(false));
        getConfig().addDefault("config.penalty-points.ban.enabled", Boolean.valueOf(true));
        getConfig().addDefault("config.penalty-points.ban.points", Integer.valueOf(3));
        getConfig().addDefault("config.penalty-points.mute.enabled", Boolean.valueOf(false));
        getConfig().addDefault("config.penalty-points.mute.points", Integer.valueOf(3));
        getConfig().addDefault("config.penalty-points.mute.seconds", Integer.valueOf(300));
        getConfig().addDefault("config.command.enabled", Boolean.valueOf(false));

        getConfig().options().copyDefaults(true);

        saveConfig();
        reloadConfig();
    }

    public void setmute(final Player player) {
        int sec = getConfig().getInt("config.penalty-points.mute.seconds");
        long seconds = 20 * sec;
        double minuites = 20 * sec / 1200;

        getCustomConfig().set("players.censorship." + player.getName() + ".muted", Boolean.valueOf(true));
        getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " is now muted for " + ChatColor.GOLD + minuites + " minuites" + ChatColor.WHITE + ".");
        saveConfig();

        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {

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