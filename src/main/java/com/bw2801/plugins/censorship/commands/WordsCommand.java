package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.actions.ReplaceAction;
import com.bw2801.plugins.censorship.actions.ReplaceActionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WordsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length != 0 && (args.length >= 1 && args[0].equalsIgnoreCase("words")))) return false;
        if (!Censorship.hasPermission(cs, "censor.list")) return true;

        cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Words ===");
        int i = 1;
        for (ReplaceAction action : ReplaceActionManager.getActions()) {
            cs.sendMessage(ChatColor.RED + "" + i + ". " + ChatColor.GOLD + action.word + ChatColor.GRAY + " (" + action.replace + ")");
            i++;
        }

        return true;
    }
}
