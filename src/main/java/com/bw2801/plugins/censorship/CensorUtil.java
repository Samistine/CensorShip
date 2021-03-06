package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.Action;
import com.bw2801.plugins.censorship.actions.CensorAction;
import com.bw2801.plugins.censorship.actions.ReplaceAction;
import com.bw2801.plugins.censorship.replace.ReplaceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CensorUtil {

    private static final HashMap<String, ReplaceUtil> replaceUtils = new HashMap<>();

    public static class CensorResult {

        public final List<CensorAction> actions;
        public final String result;
        public final String original;

        public CensorResult(List<CensorAction> actions, String result, String original) {
            this.actions = actions;
            this.result = result;
            this.original = original;
        }

        @Override
        public String toString() {
            return "CensorResult{result=" + result + ", original=" + original + "}";
        }
    }

    public static CensorResult censor(String msg, Set<ReplaceAction> replaceActions) {
        String result = msg;

        List<CensorAction> actions = new ArrayList<>();

        for (ReplaceAction action : replaceActions) {
            result = replace(result, action.word, action.method);
        }

        StringTokenizer st = new StringTokenizer(result);
        String string = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            for (ReplaceAction action : replaceActions) {
                boolean contains = false;
                for (String exception : action.exceptions) {
                    if (token.toLowerCase().contains(exception.toLowerCase())) {
                        contains = true;
                    }
                }

                if (!contains) {
                    if (token.contains(action.word)) {
                        List<Integer> positions = new ArrayList<>();

                        for (int i = 0; i < token.length(); i++) {
                            if (Character.isUpperCase(token.charAt(i))) {
                                positions.add(i);
                            }
                        }

                        if (token.toLowerCase().contains(action.word.toLowerCase())) {
                            token = token.toLowerCase().replaceAll(action.word.toLowerCase(), " " + action.replace.toLowerCase());
                        }

                        for (int i : positions) {
                            if (i < token.length()) {
                                Character.toUpperCase(token.charAt(i));
                            }
                        }

                        CensorAction ca = new CensorAction(action.word, action.commands, action.penaltyPoints, action.damage, action.action);
                        actions.add(ca);
                    }
                }
            }
            string += token + " ";
        }
        string = string.trim();

        return new CensorResult(actions, string, msg);
    }

    public static void execute(CensorResult result, Player player) {
        if (player.hasPermission("censor.bypass.actions")) {
            return;
        }

        if (Config.isNotifyEnabled()) {
            for (Player op : Bukkit.getOnlinePlayers()) {
                if (op.isOp() && Config.getMessage("notify").length() != 0) {
                    op.sendMessage(Config.getMessage("notify").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                }
            }
        }

        for (CensorAction action : result.actions) {
            if (Config.isDamageEnabled()) {
                int damage = action.damage;

                if (player.getHealth() >= damage) {
                    player.setHealth(player.getHealth() - damage);
                } else {
                    player.setHealth(0);
                }

                if (damage != 0 && Config.getMessage("damaged").length() != 0) {
                    Censorship.sendMessage(player, Config.getMessage("damaged"));
                }
            }

            PlayerHandler.addPenaltyPoints(player.getName(), action.penaltyPoints);

            if (action.action == Action.BAN) {
                player.kickPlayer(Config.getMessage("banned"));

                if (Config.getMessage("banned-public").length() != 0) {
                    Bukkit.broadcastMessage(Config.getMessage("banned-public").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                }
                player.setBanned(true);
            } else if (action.action == Action.KICK) {
                player.kickPlayer(Config.getMessage("kicked"));

                if (Config.getMessage("kicked-public").length() != 0) {
                    Bukkit.broadcastMessage(Config.getMessage("kicked-public").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                }
            } else if (action.action == Action.TEMPBAN) {
                PlayerHandler.tempBanPlayer(player.getName(), Config.getTempBanTime());
                player.kickPlayer(Config.getMessage("tempbanned").replaceAll("<minutes>", ChatColor.RED + "" + (Config.getTempBanTime() / 60) + ChatColor.WHITE));

                if (Config.getMessage("tempbanned-public").length() != 0) {
                    Bukkit.broadcastMessage(Config.getMessage("tempbanned-public")
                            .replaceAll("<minutes>", ChatColor.RED + "" + (Config.getTempBanTime() / 60) + ChatColor.WHITE).replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                }
            }

            if (Config.isTempBanEnabled()) {
                if (PlayerHandler.getPenaltyPoints(player.getName()) >= Config.getTempBanPenaltyPoints()) {
                    PlayerHandler.tempBanPlayer(player.getName(), Config.getTempBanTime());
                    player.kickPlayer(Config.getMessage("tempbanned").replaceAll("<minutes>", ChatColor.RED + "" + (Config.getTempBanTime() / 60) + ChatColor.WHITE));

                    if (Config.getMessage("tempbanned-public").length() != 0) {
                        Bukkit.broadcastMessage(Config.getMessage("tempbanned-public")
                                .replaceAll("<minutes>", ChatColor.RED + "" + (Config.getTempBanTime() / 60) + ChatColor.WHITE).replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                    }
                }
            }

            if (Config.isBanEnabled()) {
                if (PlayerHandler.getPenaltyPoints(player.getName()) >= Config.getBanPenaltyPoints()) {
                    player.kickPlayer(Config.getMessage("overused-banned"));

                    if (Config.getMessage("overused-banned-public").length() != 0) {
                        Bukkit.broadcastMessage(Config.getMessage("overused-banned-public").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE));
                    }

                    player.setBanned(true);
                }
            }

            if (Config.isMuteEnabled()) {
                if (PlayerHandler.getPenaltyPoints(player.getName()) >= Config.getMutePenaltyPoints()) {
                    if (Config.getMessage("muted-public").length() != 0) {
                        Bukkit.broadcastMessage(Config.getMessage("muted-public").replaceAll("<player>", ChatColor.GOLD + player.getName() + ChatColor.WHITE).replaceAll("<minutes>", ChatColor.RED + "" + Config.getMuteTime() / 60 + ChatColor.WHITE));
                    }

                    PlayerHandler.mutePlayer(player.getName(), Config.getMuteTime());
                }
            }

            if (Config.isCommandsEnabled()) {
                for (String command : action.commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("<player>", player.getName()));
                }
            }
        }
    }

    public static String replace(String source, String search, String method) {
        return replaceUtils.get(method).replace(source, search);
    }

    public static void addReplaceUtil(String name, ReplaceUtil util) {
        replaceUtils.put(name, util);
        Censorship.print("Info", "Added replace method: \"" + name + "\"");
    }

    public static Set<String> getReplaceUtils() {
        return replaceUtils.keySet();
    }
}
