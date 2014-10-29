package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.CensorUtil.CensorResult;
import com.bw2801.plugins.censorship.actions.ReplaceActionManager;
import java.util.StringTokenizer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (PlayerHandler.isTempBanned(event.getName())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            Config.getMessage("tempbanned-login").replaceAll("<minutes>", ChatColor.RED + "" + PlayerHandler.getTempBanTime(event.getName()) / 60 + ChatColor.WHITE));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        if (Config.isSignsEnabled() && !event.getPlayer().hasPermission("censor.bypass.censor")) {
            for (int i=0; i < 4; i++) {
                String line = event.getLine(i);

                CensorResult result = CensorUtil.censor(line, ReplaceActionManager.getActions());
                CensorUtil.execute(result, event.getPlayer());
                line = result.result;

                event.setLine(i, line);
                event.getBlock().getState().update();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!Config.isCheckCommandsEnabled() || event.getPlayer().hasPermission("censor.bypass.censor")) return;

        String cmd = "";
        String msg = "";
        String rest = "";
        int pos = -1;

        for (String command : Config.getCommands()) {
            msg = command;
            String split[] = msg.split(" ");
            String emsg[] = event.getMessage().toLowerCase().split(" ");

            if (emsg[0].equals(split[0].toLowerCase())) {
                cmd = split[0];
                String split2[] = event.getMessage().replaceFirst(cmd + " ", "").split(" ");

                for (String s : split2) {
                    rest += s + " ";
                }

                break;
            }
        }

        String split[] = msg.replaceFirst(cmd + " ", "").split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("<msg>")) {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            String check = rest;
            rest = "";

            StringTokenizer st = new StringTokenizer(check);
            String string = "";

            int i = 0;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();

                if (i > pos) {
                    string += token + " ";
                } else {
                    rest += token + " ";
                }
                i++;
            }

            CensorResult result = CensorUtil.censor(string, ReplaceActionManager.getActions());
            CensorUtil.execute(result, event.getPlayer());
            String s = result.result;
            event.setMessage(cmd + " " + rest + s);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (PlayerHandler.isMuted(event.getPlayer().getName()) && !event.getPlayer().hasPermission("censor.bypass.actions")) {
            if (Config.getMessage("muted").length() != 0) {
                Censorship.sendMessage(event.getPlayer(), Config.getMessage("muted"));
            }

            event.setCancelled(true);
            return;
        }

        if (event.getPlayer().hasPermission("censor.bypass.censor")) return;

        String msg = event.getMessage();
        CensorResult result = CensorUtil.censor(msg, ReplaceActionManager.getActions());
        CensorUtil.execute(result, event.getPlayer());

        event.setMessage(result.result);
    }
}