package com.bw2801.plugins.censorship;

import java.util.StringTokenizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CensorShipListener implements Listener {

    private Censorship main;

    public CensorShipListener(Censorship main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean muted = main.getConfig().getBoolean("players.censorship." + event.getPlayer() + ".muted");

        if (muted == true) {
            main.setMute(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent event) {
        if (main.getConfig().getBoolean("config.signs.enabled", true)) {
            for (int i = 0; i < 4; i++) {
                String line = event.getLine(i);
                line = main.replace(line, event.getPlayer());
                event.setLine(i, line);
                event.getBlock().getState().update();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!main.getConfig().getBoolean("config.check-commands.enabled", false)) {
            return;
        }

        String cmd = "";
        String msg = "";
        String rest = "";
        int pos = -1;

        for (String command : main.getConfig().getStringList("config.check-commands.list")) {
            msg = command;
            String split[] = msg.split(" ");
            String[] emsg = event.getMessage().toLowerCase().split(" ");
            if (emsg[0].equals(split[0].toLowerCase())) {
                cmd = split[0];
                String split2[] = event.getMessage().replaceFirst(cmd + " ", "").split(" ");
                for (int i = 0; i < split2.length; i++) {
                    rest += split2[i] + " ";
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
                if (i >= pos) {
                    string += token + " ";
                } else {
                    rest += token + " ";
                }
                i++;
            }

            String string2 = main.replace(string, event.getPlayer());
            event.setMessage(cmd + " " + rest + string2);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        boolean isMuted = false;
        try {
            isMuted = main.getCustomConfig().getBoolean("players.censorship." + event.getPlayer().getName() + ".muted", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isMuted) {
            event.getPlayer().sendMessage(main.getMessageConfig().getString("messages.muted"));
            event.setCancelled(true);
        } else {
            event.setMessage(main.replace(msg, event.getPlayer()));
        }
    }
}