package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length == 2 && args[0].equalsIgnoreCase("clear"))) return false;
        if (!(Censorship.hasPermission(cs, "censor.clear"))) return true;

        PlayerHandler.tempBanPlayer(args[1], 0);
        PlayerHandler.mutePlayer(args[1], 0);

        cs.sendMessage(ChatColor.WHITE + "Successfully cleared " + ChatColor.GOLD + args[1] + ChatColor.WHITE + " from effects.");
        return true;
    }
}
