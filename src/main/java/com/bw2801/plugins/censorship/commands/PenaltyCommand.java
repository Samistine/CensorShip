package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import com.bw2801.plugins.censorship.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PenaltyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(args.length >= 3 && (args.length >= 1 && args[0].equalsIgnoreCase("penalty")))) return false;

        if (args.length == 4 && args[1].equalsIgnoreCase("add")) {
            if (!Censorship.hasPermission(cs, "censor.penalty-points.add")) return true;
            try {
                PlayerHandler.addPenaltyPoints(args[2], Integer.parseInt(args[3]));
                System.out.println(ChatColor.WHITE + "Successfully added \"" + ChatColor.RED + args[3] + ChatColor.WHITE + "\" penalty points for \"" + ChatColor.GOLD + args[2] + ChatColor.WHITE + "\".");
            } catch(NumberFormatException ex) {
                System.out.println(ChatColor.RED + "Could not add \"" + args[3] + "\" penalty points for \"" + args[2] + "\".");
            }
        } else if (args.length == 4 && args[1].equalsIgnoreCase("remove")) {
            if (!Censorship.hasPermission(cs, "censor.penalty-points.remove")) return true;
            try {
                PlayerHandler.removePenaltyPoints(args[2], Integer.parseInt(args[3]));
                System.out.println(ChatColor.WHITE + "Successfully removed \"" + ChatColor.RED + args[3] + ChatColor.WHITE + "\" penalty points from \"" + ChatColor.GOLD + args[2] + ChatColor.WHITE + "\".");
            } catch(NumberFormatException ex) {
                System.out.println(ChatColor.RED + "Could not remove \"" + args[3] + "\" penalty points from \"" + args[2] + "\".");
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("get")) {
            if (!Censorship.hasPermission(cs, "censor.penalty-points.get")) return true;
            System.out.println(ChatColor.GOLD + args[2] + ChatColor.WHITE + " has " + ChatColor.RED + PlayerHandler.getPenaltyPoints(args[2]) + ChatColor.WHITE + " penalty points.");
        } else {
            return false;
        }

        return true;
    }
}
