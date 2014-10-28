package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.WordHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length > 1 && args[0].equalsIgnoreCase("remove"))) return false;
        if (!Censorship.hasPermission(cs, "censor.remove")) return false;

        if (args[1].equalsIgnoreCase("exception")) {
            String word = args[2];
            String exception = args[3];

            if (WordHandler.removeException(word, exception)) {
                cs.sendMessage("The exception \"" + ChatColor.GOLD + exception + ChatColor.WHITE + "\" for \"" + ChatColor.GOLD + word + ChatColor.WHITE + "\" has successfully been removed.");
            } else {
                cs.sendMessage(ChatColor.RED + "The exception \"" + ChatColor.GOLD + exception + ChatColor.RED + "\" could not be removed.");
            }

            return true;
        } else {
            String word = args[1];

            if (WordHandler.removeWord(word)) {
                cs.sendMessage("The word \"" + ChatColor.GOLD + word + ChatColor.WHITE + "\" has successfully been removed.");
            } else {
                cs.sendMessage(ChatColor.RED + "The word \"" + ChatColor.GOLD + word + ChatColor.RED + "\" could not be removed.");
            }

            return true;
        }
    }
}
