package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.WordHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length >= 4 && args[0].equalsIgnoreCase("update"))) return false;
        if (!Censorship.hasPermission(cs, "censor.update")) return true;

        if (args[2].equalsIgnoreCase("replace")) {
            if (WordHandler.updateWord(args[1], args[3])) {
                cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
            } else {
                cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
            }
        } else if (args[2].equalsIgnoreCase("action")) {
            if (WordHandler.updateAction(args[1], args[3])) {
                cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
            } else {
                cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
            }
        } else if (args[2].equalsIgnoreCase("method")) {
            if (WordHandler.updateMethod(args[1], args[3])) {
                cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
            } else {
                cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
            }
        } else if (args[2].equalsIgnoreCase("penalty")) {
            boolean done = false;

            try {
                String[] penalty = args[3].split(":");
                done = WordHandler.updatePenalty(args[1], Integer.parseInt(penalty[0]), Integer.parseInt(penalty[1]));
            } catch (NullPointerException | NumberFormatException ex) {
            }

            if (done) {
                cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
            } else {
                cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
            }
        } else {
            String word = args[1];
            String replace = args[2];
            String action = args[3];

            String method = null;
            int damage = -1;
            int points = -1;

            if (args.length > 4) {
                method = args[4];
            }

            if (args.length == 5) {
                if (WordHandler.updateWord(word, replace, action, method)) {
                    cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
                } else {
                    cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
                }
            } else if (args.length == 6) {
                try {
                    String[] penalty = args[3].split(":");
                    damage = Integer.parseInt(penalty[0]);
                    points = Integer.parseInt(penalty[1]);
                } catch (NullPointerException | NumberFormatException ex) {
                    cs.sendMessage(ChatColor.RED + "Could not read values for damage and penalty points.");
                    return true;
                }

                if (WordHandler.updateWord(word, replace, action, method, damage, points)) {
                    cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
                } else {
                    cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
                }
            } else {
                if (WordHandler.updateWord(word, replace, action)) {
                    cs.sendMessage(ChatColor.WHITE + "Successfully updated \"" + ChatColor.GOLD + args[1] + ChatColor.WHITE + "\".");
                } else {
                    cs.sendMessage(ChatColor.RED + "Could not update \"" + ChatColor.GOLD + args[1] + ChatColor.RED + "\".");
                }
            }
        }

        return true;
    }
}
