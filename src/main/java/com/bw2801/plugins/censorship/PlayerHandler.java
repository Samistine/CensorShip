package com.bw2801.plugins.censorship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerHandler {

    private static final Map<String, Integer> penaltyPoints = new HashMap<>();
    private static final Map<String, Integer> muteTime = new HashMap<>();
    private static final Map<String, Integer> banTime = new HashMap<>();

    public static boolean isMuted(String player) {
        return muteTime.containsKey(player) && muteTime.get(player) > 0;
    }

    public static int getMuteTime(String player) {
        if (!muteTime.containsKey(player)) return 0;

        return muteTime.get(player);
    }

    public static int getPenaltyPoints(String player) {
        if (!penaltyPoints.containsKey(player)) return 0;

        return penaltyPoints.get(player);
    }

    public static void setPenaltyPoints(String player, int points) {
        penaltyPoints.put(player, points);
    }

    public static void addPenaltyPoints(String player, int points) {
        penaltyPoints.put(player, getPenaltyPoints(player) + points);
    }

    public static void removePenaltyPoints(String player, int points) {
        penaltyPoints.put(player, getPenaltyPoints(player) - points);
    }

    public static void mutePlayer(String player, int seconds) {
        muteTime.put(player, seconds);
    }

    public static void tempBanPlayer(String player, int seconds) {
        banTime.put(player, seconds);
    }

    public static int getTempBanTime(String player) {
        if (!banTime.containsKey(player)) return 0;

        return banTime.get(player);
    }

    public static boolean isTempBanned(String player) {
        return getTempBanTime(player) > 0;
    }

    public static void decreaseTempBanTime(String player, int seconds) {
        banTime.put(player, getTempBanTime(player) - seconds);
    }

    public static Set<String> getPlayers() {
        Set<String> players = penaltyPoints.keySet();

        for (String player : muteTime.keySet()) {
            if (!players.contains(player)) {
                players.add(player);
            }
        }

        for (String player : banTime.keySet()) {
            if (!players.contains(player)) {
                players.add(player);
            }
        }

        return players;
    }

    public static Set<String> getTempBannedPlayers() {
        Set<String> players = new HashSet<>();

        for (String player : banTime.keySet()) {
            if (isTempBanned(player)) {
                players.add(player);
            }
        }

        return players;
    }
}
