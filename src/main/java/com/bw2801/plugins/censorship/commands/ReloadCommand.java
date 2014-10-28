package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length > 0 && args[0].equalsIgnoreCase("reload"))) return false;
        if (!Censorship.hasPermission(cs, "censor.reload")) return false;

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Censorship.reload();
            cs.sendMessage(ChatColor.GOLD + "CensorChat" + ChatColor.WHITE + " has successfully been reloaded.");
            return true;
        }

        return false;
    }
}
