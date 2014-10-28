package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.WordHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length > 3 && args[0].equalsIgnoreCase("add"))) return false;
        if (!Censorship.hasPermission(cs, "censor.add")) return false;

        if (args[1].equalsIgnoreCase("exception")) {
            String word = args[2];
            String exception = args[3];

            if (WordHandler.addException(word, exception)) {
                cs.sendMessage("The exception \"" + ChatColor.GOLD + exception + ChatColor.WHITE + "\" for \"" + ChatColor.GOLD + word + ChatColor.WHITE + "\" has successfully been added.");
            } else {
                cs.sendMessage(ChatColor.RED + "The exception \"" + ChatColor.GOLD + exception + ChatColor.RED + "\" could not be added.");
            }

            return true;
        } else {
            String word = args[1];
            String replace = args[2];
            String action = args[3];

            String method = "default";
            int damage = 0;
            int pp = 0;

            if (args.length >= 5) {
                method = args[4];
            }

            if (args.length >= 6) {
                String[] split = args[5].split(":");
                damage = Integer.parseInt(split[0]);
                pp = Integer.parseInt(split[1]);
            }

            if (WordHandler.addWord(word, replace, action, method, damage, pp)) {
                cs.sendMessage("The word \"" + ChatColor.GOLD + word + ChatColor.WHITE + "\" has successfully been added.");
            } else {
                cs.sendMessage(ChatColor.RED + "The word \"" + ChatColor.GOLD + word + ChatColor.RED + "\" could not be added.");
            }

            return true;
        }
    }
}
