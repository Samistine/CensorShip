package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.Action;
import com.bw2801.plugins.censorship.actions.CensorAction;
import com.bw2801.plugins.censorship.actions.ReplaceAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CensorUtil {

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
            result = replace(result, action.word);
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
                if (op.isOp()) {
                    op.sendMessage(player.getName() + " used forbidden word(s)."); // TODO: maybe show message (for admins)
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

                if (damage != 0) {
                    // TODO: show message (damaged)
                }
            }

            PlayerHandler.addPenaltyPoints(player.getName(), action.penaltyPoints);

            if (action.action == Action.BAN) {
                player.kickPlayer(""); // TODO: show message (banned)
                player.setBanned(true);
                // TODO: show message (broadcast banned public)
            } else if (action.action == Action.KICK) {
                player.kickPlayer(""); // TODO: show message (kicked)
                // TODO: show message (broadcast kicked public)
            }

            if (Config.isBanEnabled()) {
                if (PlayerHandler.getPenaltyPoints(player.getName()) >= Config.getBanPenaltyPoints()) {
                    player.kickPlayer(""); // TODO: show message (banned for overusing)
                    player.setBanned(true);
                }
            }

            if (Config.isMuteEnabled()) {
                if (PlayerHandler.getPenaltyPoints(player.getName()) >= Config.getMutePenaltyPoints()) {
                    // TODO: show message (muted for overusing)
                    PlayerHandler.mutePlayer(player.getName(), Config.getMuteTime());
                }
            }

            if (Config.isCommandsEnabled()) {
                for (String command : action.commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }

    public static String replace(String source, String search) {
        int length = search.length();
        if (length < 2) {
            return source;
        }

        StringBuilder sb = new StringBuilder(4 * length - 3);
        for (int i = 0; i < length - 1; i++) {
            sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
        }
        sb.append("([\\W\\d\\s]*)+");
        sb.append(search.charAt(length - 1));

        String temp = source.replaceAll("(?i)" + sb.toString(), search).trim();
        int wordCount = temp.split("\\s").length;

        if (wordCount == 1 || wordCount == source.split("\\s").length) {
            return temp;
        } else {
            System.out.println(temp);

            sb = new StringBuilder(4 * length - 3);
            sb.append("\\s+");

            for (int i = 0; i < length - 1; i++) {
                sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
            }
            sb.append("([\\W\\d]*\\s)");
            sb.append(search.charAt(length - 1));
        }

//        System.out.println(temp);
//        System.out.println(temp.split("\\s").length);

//        if (temp.split("\\s").length == 1 || temp.split("\\s").length == source.split("\\s").length) {
//            return temp.trim();
//        } else {
//            sb = new StringBuilder(4 * length - 3);
//            sb.append("\\s+");
//
//            for (int i = 0; i < length - 1; i++) {
//                sb.append("([\\W\\d]*").append(Pattern.quote("" + search.charAt(i))).append(")+");
//            }
//            sb.append("([\\W\\d]*\\s)");
//            sb.append(search.charAt(length - 1));
//        }

        String replace = source;

        if (wordCount <= 2) {
            replace = " " + source;
        }

        String result = replace.replaceAll("(?i)" + sb.toString(), search).trim();
        return result;
    }
}
