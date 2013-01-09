package com.bw2801.plugins.censorship;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CensorShipAPI {

    /**
     * This method replaces the a string and exectues the actions defined in the
     * censorship configuration file like ban, kick, mute, adding penalty-points
     * and so on...
     * @param msg the string that have to be censored
     * @param player the player which is the actions target
     * @return the censored and replaced string
     */
    public static String replace(String msg, Player player) {
        ConfigurationSection sec = Censorship.plugin.getConfig().getConfigurationSection("config.censorship");
        String result = msg;
        List<String> actions = new ArrayList<String>();

        if (sec != null) {
            for (String search : sec.getKeys(false)) {
                result = Censorship.replace(result, search);
            }
        }

        StringTokenizer st = new StringTokenizer(result);
        String output = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    boolean contains = false;
                    for (String item : Censorship.plugin.getConfig().getStringList("config.censorship." + key.toLowerCase() + ".exceptions")) {
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
                                if (Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".action") != null) {
                                    action = Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".action");
                                }
                                if (Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".command") != null) {
                                    cmd = Censorship.plugin.getConfig().getStringList("config.censorship." + key.toLowerCase() + ".command");
                                }
                                if (Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".replace-with") != null) {
                                    replaceWith = Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".replace-with");
                                }
                                if (Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".mcbans.reason") != null) {
                                    rs = Censorship.plugin.getConfig().getString("config.censorship." + key.toLowerCase() + ".mcbans.reason");
                                }
                                pp = Censorship.plugin.getConfig().getInt("config.censorship." + key.toLowerCase() + ".penalty-points");
                                dmg = Censorship.plugin.getConfig().getInt("config.censorship." + key.toLowerCase() + ".damage");
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
            output = output + token + " ";
        }

        if (player != null) {
            if (!player.hasPermission("censor.bypass")) {

                int mp = Censorship.plugin.getConfig().getInt("config.penalty-points.mute.points");
                int bp = Censorship.plugin.getConfig().getInt("config.penalty-points.ban.points");

                for (String action : actions) {
                    if (action.startsWith("cmd:") && Censorship.plugin.getConfig().getBoolean("config.command.enabled")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("cmd:", ""));
                    }
                    if (Censorship.plugin.getConfig().getBoolean("config.notify.enabled")) {
                        for (Player player2 : Censorship.plugin.getServer().getOnlinePlayers()) {
                            if (player2.isOp()) {
                                player2.sendMessage(player.getName() + " used forbidden word(s).");
                            }
                        }
                    }
                    if (action.startsWith("dmg:") && Censorship.plugin.getConfig().getBoolean("config.damage.enabled")) {
                        int hit = Integer.parseInt(action.replace("dmg:", ""));

                        if (player.getHealth() >= hit) {
                            player.setHealth(player.getHealth() - hit);
                        } else {
                            player.setHealth(0);
                        }

                        if (hit != 0) {
                            player.sendMessage("You were damaged for using forbidden word(s).");
                        }
                    }
                    if (action.equals("ban")) {
                        player.setBanned(true);
                        player.kickPlayer("You were banned for using forbidden word(s) in chat.");
                        Censorship.plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " was kicked for using forbidden word(s).");
                    } else if (action.equals("kick")) {
                        player.kickPlayer("You were kicked for using forbidden word(s) in chat.");
                        Censorship.plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.WHITE + " was kicked for using forbidden word(s).");
                    }
                    if (action.startsWith("pp:")) {
                        int points = Integer.parseInt(action.replace("pp:", ""));
                        points += Censorship.plugin.getCustomConfig().getInt("players.censorship." + player.getName() + ".penalty-points");

                        if (points >= bp) {
                            if (Censorship.plugin.getConfig().getBoolean("config.penalty-points.ban.enabled")) {
                                player.kickPlayer("You were banned, because of overusing forbidden words.");
                                player.setBanned(true);
                                Censorship.plugin.getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                                Censorship.plugin.saveCustomConfig();
                            }
                        } else {
                            Censorship.plugin.getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                            Censorship.plugin.saveCustomConfig();
                        }

                        if (Censorship.plugin.getConfig().getBoolean("config.penalty-points.mute.enabled")) {
                            if (points >= mp) {
                                Censorship.plugin.getCustomConfig().set("players.censorship." + player.getName() + ".penalty-points", points);
                                Censorship.plugin.setMute(player);
                                Censorship.plugin.saveCustomConfig();
                            }
                        }
                    }
                }
            }
        }

        return output;
    }

    /**
     * This method replaces the string and does nothing else.
     * @param msg the string that have to be replaced
     * @return the censored and replaced string
     */
    public static String replace(String msg) {
        return replace(msg, null);
    }

    /**
     * This method sets a option in the config of CensorShip to enabled or
     * disabled (when your plugin needs special settings to work with this one).
     * @param option the configuration file option that should be changed
     * @param activated wether the option should be enabled or disabled
     * @return wether it was successfull
     */
    public static boolean setOption(String option, boolean activated) {
        if (Censorship.plugin.getConfig().getString("config." + option, null) != null) {
            Censorship.plugin.getConfig().set("config." + option, activated);
            if (Censorship.plugin.getConfig().getBoolean("config." + option) == activated) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    /**
     * This method gets some information about the plugin (name, version and
     * author).
     * @return information about the plugin
     */
    public static String info() {
        return "Name: CensorShip\n"
                + "Version: 2.1\n"
                + "Author: Bw2801\n";
    }
}