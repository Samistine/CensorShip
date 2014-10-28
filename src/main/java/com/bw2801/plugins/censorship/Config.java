package com.bw2801.plugins.censorship;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private static boolean damageEnabled = false;
    private static boolean notifyEnabled = false;
    private static boolean signsEnabled = false;
    private static boolean commandsEnabled = false;
    private static boolean checkCommands = false;
    private static boolean muteEnabled = false;
    private static boolean banEnabled = false;
    private static boolean tempBanEnabled = false;

    private static String wordSaveFile = "words.json";
    private static int autoSaveWordsInteval = 0;

    private static int mutePenaltyPoints = 3;
    private static int banPenaltyPoints = 3;
    private static int tempBanPenaltyPoints = 3;

    private static int tempBanTime = 86400;
    private static int muteTime = 300;

    private static Censorship plugin;

    private static List<String> commands = new ArrayList<>();

    public static void init(Censorship main) {
        plugin = main;
    }

    public static int getAutoSaveWordsInteval() {
        return autoSaveWordsInteval;
    }

    public static void setAutoSaveWordsInteval(int autoSaveWordsInteval) {
        Config.autoSaveWordsInteval = autoSaveWordsInteval;
    }

    public static void setWordSaveFile(String save) {
        wordSaveFile = save;
    }

    public static String getWordSaveFile() {
        return wordSaveFile;
    }

    private static void loadMessages() {
        FileConfiguration config = plugin.getMessageConfig();
        config.addDefault("messages.no-permission", "You don't have permissions to perform this command.");
        config.addDefault("messages.damaged", "You were damaged for using forbidden words.");
        config.addDefault("messages.banned", "You were banned for using forbidden words.");
        config.addDefault("messages.banned-public", "<player> was banned for using forbidden words.");
        config.addDefault("messages.tempbanned-login", "You are banned for the next <time> minutes.");
        config.addDefault("messages.tempbanned", "You were banned for <time> minutes for using forbidden words.");
        config.addDefault("messages.tempbanned-public", "<player> was banned for <time> minutes for using forbidden words.");
        config.addDefault("messages.kicked", "You were kicked for using forbidden words.");
        config.addDefault("messages.kicked-public", "<player> was kicked for using forbidden words.");
        config.addDefault("messages.overused-banned", "You were banned for overusing forbidden words.");
        config.addDefault("messages.overused-banned-public", "<player> was banned for overusing forbidden words.");
        config.addDefault("messages.muted", "You are muted! Nobody can hear you.");
        config.addDefault("messages.muted-public", "<player> is now muted for <time> minutes.");
        config.addDefault("messages.unmuted", "You are no longer muted.");
        config.addDefault("messages.unmuted-public", "<player> is no longer muted.");
        config.addDefault("messages.notify", "<player> used forbidden words.");
        config.options().copyDefaults(true);
        plugin.saveMessageConfig();
    }

    private static void loadPlayerConfig() {
        String players = "players";
        plugin.getPlayerConfig().addDefault(players, null);

        plugin.getPlayerConfig().options().copyDefaults(true);
        plugin.savePlayerConfig();

        ConfigurationSection sec = plugin.getPlayerConfig().getConfigurationSection("players");

        if (sec == null) return;

        for (Object key : sec.getKeys(false)) {
            String player = key.toString();

            int time = plugin.getPlayerConfig().getInt("players." + player + ".mute-time", 0);
            if (time > 0) {
                PlayerHandler.mutePlayer(player, time);
            }

            PlayerHandler.setPenaltyPoints(player, plugin.getPlayerConfig().getInt("players." + player + ".penalty-points", 0));
            PlayerHandler.tempBanPlayer(player, plugin.getPlayerConfig().getInt("players." + player + ".ban-time", 0));
        }
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

    public static boolean isTempBanEnabled() {
        return tempBanEnabled;
    }

    public static void setTempBanEnabled(boolean tempBanEnabled) {
        Config.tempBanEnabled = tempBanEnabled;
    }

    public static int getTempBanPenaltyPoints() {
        return tempBanPenaltyPoints;
    }

    public static void setTempBanPenaltyPoints(int tempBanPenaltyPoints) {
        Config.tempBanPenaltyPoints = tempBanPenaltyPoints;
    }

    public static int getTempBanTime() {
        return tempBanTime;
    }

    public static void setTempBanTime(int tempBanTime) {
        Config.tempBanTime = tempBanTime;
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
        plugin.getConfig().addDefault("config.damage.enabled", false);
        plugin.getConfig().addDefault("config.notify.enabled", false);
        plugin.getConfig().addDefault("config.signs.enabled", true);
        plugin.getConfig().addDefault("config.penalty-points.ban.enabled", false);
        plugin.getConfig().addDefault("config.penalty-points.ban.points", 10);
        plugin.getConfig().addDefault("config.penalty-points.tempban.enabled", false);
        plugin.getConfig().addDefault("config.penalty-points.tempban.points", 7);
        plugin.getConfig().addDefault("config.penalty-points.tempban.time", 86400);
        plugin.getConfig().addDefault("config.penalty-points.mute.enabled", false);
        plugin.getConfig().addDefault("config.penalty-points.mute.points", 3);
        plugin.getConfig().addDefault("config.penalty-points.mute.seconds", 300);
        plugin.getConfig().addDefault("config.commands.enabled", false);
        plugin.getConfig().addDefault("config.check-commands.enabled", false);
        plugin.getConfig().addDefault("config.check-commands.list", new ArrayList<String>());
        plugin.getConfig().options().copyDefaults(true);

        plugin.saveConfig();
        plugin.reloadConfig();

        damageEnabled = plugin.getConfig().getBoolean("config.damage.enabled", false);
        notifyEnabled = plugin.getConfig().getBoolean("config.notify.enabled", false);
        signsEnabled = plugin.getConfig().getBoolean("config.signs.enabled", false);
        commandsEnabled = plugin.getConfig().getBoolean("config.commands.enabled", false);

        banEnabled = plugin.getConfig().getBoolean("config.penalty-points.ban.enabled", false);
        banPenaltyPoints = plugin.getConfig().getInt("config.penalty-points.ban.points", 3);

        tempBanEnabled = plugin.getConfig().getBoolean("config.penalty-points.tempban.enabled", false);
        tempBanPenaltyPoints = plugin.getConfig().getInt("config.penalty-points.tempban.points", 3);
        tempBanTime = plugin.getConfig().getInt("config.penalty-points.tempban.time", 86400);

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

        plugin.getConfig().set("config.penalty-points.tempban.enabled", tempBanEnabled);
        plugin.getConfig().set("config.penalty-points.tempban.points", tempBanPenaltyPoints);
        plugin.getConfig().set("config.penalty-points.tempban.time", tempBanTime);

        plugin.getConfig().set("config.penalty-points.mute.enabled", muteEnabled);
        plugin.getConfig().set("config.penalty-points.mute.points", mutePenaltyPoints);
        plugin.getConfig().set("config.penalty-points.mute.seconds", muteTime);

        plugin.getConfig().set("config.check-commands.enabled", checkCommands);
        plugin.getConfig().set("config.check-commands.list", commands);

        plugin.saveConfig();
        plugin.reloadConfig();

        plugin.saveMessageConfig();
        plugin.reloadMessageConfig();

        savePlayers();
        plugin.savePlayerConfig();
        plugin.reloadPlayerConfig();
    }

    private static void savePlayers() {
        for (String player : PlayerHandler.getPlayers()) {
            plugin.getPlayerConfig().set("players." + player + ".mute-time", PlayerHandler.getMuteTime(player));
            plugin.getPlayerConfig().set("players." + player + ".penalty-points", PlayerHandler.getPenaltyPoints(player));
            plugin.getPlayerConfig().set("players." + player + ".ban-time", PlayerHandler.getTempBanTime(player));
        }
    }
}
