package com.bw2801.plugins.censorship;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Config {

    private static boolean damageEnabled = false;
    private static boolean notifyEnabled = false;
    private static boolean signsEnabled = false;
    private static boolean commandsEnabled = false;
    private static boolean checkCommands = false;
    private static boolean muteEnabled = false;
    private static boolean banEnabled = false;

    private static int mutePenaltyPoints = 3;
    private static int banPenaltyPoints = 3;
    private static int muteTime = 300;

    private static Censorship plugin;

    private static List<String> commands = new ArrayList<>();

    public static void init(Censorship main) {
        plugin = main;
    }

    private static void loadMessages() {
        FileConfiguration config = plugin.getMessageConfig();
        config.addDefault("messages.no-permission", "You don't have permissions to perform this command.");
        config.addDefault("messages.damaged", "You were damaged for using forbidden word(s).");
        config.addDefault("messages.banned", "You were banned for using forbidden word(s).");
        config.addDefault("messages.banned-public", "<player> was banned for using forbidden word(s).");
        config.addDefault("messages.kicked", "You were kicked for using forbidden word(s).");
        config.addDefault("messages.kicked-public", "<player> was kicked for using forbidden word(s).");
        config.addDefault("messages.overused-banned", "You were banned for overusing forbidden words.");
        config.addDefault("messages.muted", "You are muted! Nobody can hear you.");
        config.addDefault("messages.muted-public", "<player> is muted now for <time> minutes.");
        config.addDefault("messages.unmuted", "You are no longer muted.");
        config.addDefault("messages.unmuted-public", "<player> is no longer muted.");
        config.options().copyDefaults(true);
        plugin.saveMessageConfig();
    }

    public static void mute(final Player player) {
        int sec = plugin.getConfig().getInt("config.penalty-points.mute.seconds");
        long seconds = 20 * sec;
        double minuites = 20 * sec / 1200;

        plugin.getPlayerConfig().set("players.censorship." + player.getName() + ".muted", false);
        plugin.getServer().broadcastMessage(getMessage("muted-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE).replace("<time>", ChatColor.GOLD + "" + minuites + ChatColor.WHITE));
        plugin.saveConfig();

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                plugin.getPlayerConfig().set("players.censorship." + player.getName() + ".muted", false);
                plugin.getServer().broadcastMessage(getMessage("unmuted-public").replace("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                plugin.savePlayerConfig();
            }
        }, seconds);
    }

    private static void loadPlayerConfig() {
        String players = "players.censorship";
        plugin.getPlayerConfig().addDefault(players, null);

        plugin.getPlayerConfig().options().copyDefaults(true);
        plugin.savePlayerConfig();
    }

    public static String getMessage(String name) {
        return plugin.getMessageConfig().getString("messages." + name);
    }

    public static boolean isDamageEnabled() {
        return damageEnabled;
    }

    public static boolean isNotifyEnabled() {
        return notifyEnabled;
    }

    public static boolean isSignsEnabled() {
        return signsEnabled;
    }

    public static boolean isCommandsEnabled() {
        return commandsEnabled;
    }

    public static boolean isCheckCommandsEnabled() {
        return checkCommands;
    }

    public static boolean isMuteEnabled() {
        return muteEnabled;
    }

    public static boolean isBanEnabled() {
        return banEnabled;
    }

    public static int getMutePenaltyPoints() {
        return mutePenaltyPoints;
    }

    public static int getBanPenaltyPoints() {
        return banPenaltyPoints;
    }

    public static int getMuteTime() {
        return muteTime;
    }

    public static void setDamageEnabled(boolean damageEnabled) {
        Config.damageEnabled = damageEnabled;
    }

    public static void setNotifyEnabled(boolean notifyEnabled) {
        Config.notifyEnabled = notifyEnabled;
    }

    public static void setSignsEnabled(boolean signsEnabled) {
        Config.signsEnabled = signsEnabled;
    }

    public static void setCommandsEnabled(boolean commandsEnabled) {
        Config.commandsEnabled = commandsEnabled;
    }

    public static void setCheckCommands(boolean checkCommands) {
        Config.checkCommands = checkCommands;
    }

    public static void setMuteEnabled(boolean muteEnabled) {
        Config.muteEnabled = muteEnabled;
    }

    public static void setBanEnabled(boolean banEnabled) {
        Config.banEnabled = banEnabled;
    }

    public static void setMutePenaltyPoints(int mutePenaltyPoints) {
        Config.mutePenaltyPoints = mutePenaltyPoints;
    }

    public static void setBanPenaltyPoints(int banPenaltyPoints) {
        Config.banPenaltyPoints = banPenaltyPoints;
    }

    public static void setMuteTime(int muteTime) {
        Config.muteTime = muteTime;
    }

    public static List<String> getCommands() {
        return commands;
    }

    public static void setCommands(List<String> commands) {
        Config.commands = commands;
    }

    public static void reload() {
        save();
        load();
    }

    public static void load() {
        String s = "First of all: thank you for downloading this plugin. Here you can find some information on how to edit this configuration file.\n\n"
                   + "The first things to modify are \"damage\", \"command\" and \"penalty-points\".\n"
                   + "If you enable \"damage\", the payer wil get hurt for using forbidden words.\n"
                   + "If you enable \"penalty-poitns: ban\", the player will get banned as soon as he reaches the given amount of penalty-points.\n"
                   + "If you enable \"penalty-points: mute\", the player will get muted as soon as he reaches the given amount of penalty-points for the given amount of seconds.\n"
                   + "If you enable \"command\", custom commands can be executed for using forbidden words.\n\n"
                   + "The next option to set is the \"notify\" one.\n"
                   + "If enabled, the plugin automatically notices all operators if a player uses forbidden words. "
                   + "To add a word you can use the command \"/censor add <word> <replace-with> <action> [penalty-points] [damage]\" or you can check out the \"words.json\" file.\n\n"
                   + "There are two options left now: \"signs\" and \"check-commands\".\n"
                   + "If you enable \"check-commands\", the plugin will also check the commands defined in the \"check-commands\"-section, that are executed by the player and replace forbidden words. "
                   + "To define a command you have to put the <msg>-tag where the message starts inside the command. This tag tells the plugin to check everything from there on to the end of the players input.\n"
                   + "So you can for example do something like this: \"/msg <name> <msg>\". "
                   + "In this case, the <name>-tag is just a placeholder and can be named whatever you want, so you can also name it <blah>, for example.\n\n"
                   + "If you enable \"signs\", the plugin will also check sign text.";
        plugin.getConfig().options().header(s);

        String[] list = {"/msg <name> <msg>", "/r <msg>"};

        plugin.getConfig().addDefault("config.damage.enabled", false);
        plugin.getConfig().addDefault("config.notify.enabled", false);
        plugin.getConfig().addDefault("config.signs.enabled", true);
        plugin.getConfig().addDefault("config.penalty-points.ban.enabled", false);
        plugin.getConfig().addDefault("config.penalty-points.ban.points", 3);
        plugin.getConfig().addDefault("config.penalty-points.mute.enabled", false);
        plugin.getConfig().addDefault("config.penalty-points.mute.points", 3);
        plugin.getConfig().addDefault("config.penalty-points.mute.seconds", 300);
        plugin.getConfig().addDefault("config.commands.enabled", false);
        plugin.getConfig().addDefault("config.check-commands.enabled", false);
        plugin.getConfig().addDefault("config.check-commands.list", list);
        plugin.getConfig().options().copyDefaults(true);

        plugin.saveConfig();
        plugin.reloadConfig();

        damageEnabled = plugin.getConfig().getBoolean("config.damage.enabled", false);
        notifyEnabled = plugin.getConfig().getBoolean("config.notify.enabled", false);
        signsEnabled = plugin.getConfig().getBoolean("config.signs.enabled", false);
        commandsEnabled = plugin.getConfig().getBoolean("config.commands.enabled", false);

        banEnabled = plugin.getConfig().getBoolean("config.penalty-points.ban.enabled", false);
        banPenaltyPoints = plugin.getConfig().getInt("config.penalty-points.ban.points", 3);

        muteEnabled = plugin.getConfig().getBoolean("config.penalty-points.mute.enabled", false);
        mutePenaltyPoints = plugin.getConfig().getInt("config.penalty-points.mute.points", 3);
        muteTime = plugin.getConfig().getInt("config.penalty-points.mute.seconds", 300);

        checkCommands = plugin.getConfig().getBoolean("config.check-commands.enabled", false);
        commands = plugin.getConfig().getStringList("config.check-commands.list");

        loadMessages();
        loadPlayerConfig();
    }

    public static void save() {
        plugin.getConfig().set("config.damage.enabled", damageEnabled);
        plugin.getConfig().set("config.notify.enabled", notifyEnabled);
        plugin.getConfig().set("config.signs.enabled", signsEnabled);
        plugin.getConfig().set("config.commands.enabled", commandsEnabled);

        plugin.getConfig().set("config.penalty-points.ban.enabled", banEnabled);
        plugin.getConfig().set("config.penalty-points.ban.points", banPenaltyPoints);

        plugin.getConfig().set("config.penalty-points.mute.enabled", muteEnabled);
        plugin.getConfig().set("config.penalty-points.mute.points", mutePenaltyPoints);
        plugin.getConfig().set("config.penalty-points.mute.seconds", muteTime);

        plugin.getConfig().set("config.check-commands.enabled", checkCommands);
        plugin.getConfig().set("config.check-commands.list", commands);

        plugin.saveConfig();
        plugin.reloadConfig();

        plugin.saveMessageConfig();
        plugin.reloadMessageConfig();

        plugin.savePlayerConfig();
        plugin.reloadPlayerConfig();
    }
}
