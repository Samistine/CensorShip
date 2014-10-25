package com.bw2801.plugins.censorship;

import java.util.HashMap;
import java.util.Map;

public class PlayerHandler {

    private static final Map<String, Integer> penaltyPoints = new HashMap<>();
    private static final Map<String, Integer> muteTime = new HashMap<>();

    public static boolean isMuted(String player) {
        return muteTime.containsKey(player) && muteTime.get(player) > 0;
    }

    public static int getMuteTime(String player) {
        return muteTime.get(player);
    }

    public static int getPenaltyPoints(String player) {
        return penaltyPoints.get(player);
    }

    public static void setPenaltyPoints(String player, int points) {
        penaltyPoints.put(player, points);
    }

    public static void addPenaltyPoints(String player, int points) {
        if (!penaltyPoints.containsKey(player)) {
            penaltyPoints.put(player, 0);
        }
        penaltyPoints.put(player, penaltyPoints.get(player) + points);
    }

    public static void removePenaltyPoints(String player, int points) {
        if (!penaltyPoints.containsKey(player)) {
            penaltyPoints.put(player, 0);
        }
        penaltyPoints.put(player, penaltyPoints.get(player) - points);
    }

    public static void mutePlayer(String player, int seconds) {
        muteTime.put(player, seconds);
    }
}
