package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (args.length != 0 && !(args.length >= 1 && args[0].equalsIgnoreCase("help"))) return false;
        if (!Censorship.hasPermission(cs, "censor.help")) return true;

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            showHelp(cs);
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("help")) {
            if (args[1].equalsIgnoreCase("add")) {
                cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Help | Add ===");
                cs.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <action> [method] [pp:dmg]" + ChatColor.GRAY + " | Adds a word");
                cs.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception>" + ChatColor.GRAY + " | Adds an exception");

                return true;
            } else if (args[1].equalsIgnoreCase("remove")) {
                cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Help | Remove ===");
                cs.sendMessage(ChatColor.GOLD + "/censor remove <word>" + ChatColor.GRAY + " | Removes a word");
                cs.sendMessage(ChatColor.GOLD + "/censor remove exception <word> <exception>" + ChatColor.GRAY + " | Removes an exception");

                return true;
            } else if (args[1].equalsIgnoreCase("update")) {
                cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Help | Update ===");
                cs.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <action> [method] [pp:dmg]" + ChatColor.GRAY + " | Updates a word");
                cs.sendMessage(ChatColor.GOLD + "/censor update <word> replace <replace-with>" + ChatColor.GRAY + " | Updates the word to replace with");
                cs.sendMessage(ChatColor.GOLD + "/censor update <word> action <action>" + ChatColor.GRAY + " | Updates the action to execute");
                cs.sendMessage(ChatColor.GOLD + "/censor update <word> method <method>" + ChatColor.GRAY + " | Updates the method to use to censor");
                cs.sendMessage(ChatColor.GOLD + "/censor update <word> penalty <pp:dmg>" + ChatColor.GRAY + " | Updates the penalty-points and damage for that word");

                return true;
            } else if (args[1].equalsIgnoreCase("penalty")) {
                cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Help | Penalty ===");
                cs.sendMessage(ChatColor.GOLD + "/censor penalty add <player> <points>" + ChatColor.GRAY + " | Adds penalty-points");
                cs.sendMessage(ChatColor.GOLD + "/censor penalty sub <player> <points>" + ChatColor.GRAY + " | Removes penalty-points");
                cs.sendMessage(ChatColor.GOLD + "/censor penalty get <player>" + ChatColor.GRAY + " | Gets the players penalty-points");

                return true;
            }

            return false;
        }

        return false;
    }

    public void showHelp(CommandSender cs) {
        if (!Censorship.hasPermission(cs, "censor.help")) return;

        cs.sendMessage(ChatColor.AQUA + "=== CensorShip | Help ===");
        cs.sendMessage(ChatColor.GOLD + "/censor" + ChatColor.GRAY + " | Shows a list of all commands.");
        cs.sendMessage(ChatColor.GOLD + "/censor help" + ChatColor.GRAY + " | Shows a list of all commands.");
        cs.sendMessage(ChatColor.GOLD + "/censor reload" + ChatColor.GRAY + " | Reloads the config and words.");
        cs.sendMessage(ChatColor.GOLD + "/censor words" + ChatColor.GRAY + " | Lists all the words to censor.");
        cs.sendMessage(ChatColor.GOLD + "/censor test <method> <word> <message>" + ChatColor.GRAY + " | Executes a method to censor on the given word.");
        cs.sendMessage(ChatColor.GOLD + "/censor add <word> ..." + ChatColor.GRAY + " | Adds a word (" + ChatColor.GOLD + "/censor help add" + ChatColor.GRAY + ")");
        cs.sendMessage(ChatColor.GOLD + "/censor add exception ..." + ChatColor.GRAY + " | Adds an exception (" + ChatColor.GOLD + "/censor help add" + ChatColor.GRAY + ")");
        cs.sendMessage(ChatColor.GOLD + "/censor remove <word> ..." + ChatColor.GRAY + " | Removes a word (" + ChatColor.GOLD + "/censor help remove" + ChatColor.GRAY + ")");
        cs.sendMessage(ChatColor.GOLD + "/censor remove exception ..." + ChatColor.GRAY + " | Removes an exception (" + ChatColor.GOLD + "/censor help remove" + ChatColor.GRAY + ")");
        cs.sendMessage(ChatColor.GOLD + "/censor update ..." + ChatColor.GRAY + " | Updates a word (" + ChatColor.GOLD + "/censor help update" + ChatColor.GRAY + ")");
        cs.sendMessage(ChatColor.GOLD + "/censor penalty ..." + ChatColor.GRAY + " | Modifies penalty-points (" + ChatColor.GOLD + "/censor help penalty" + ChatColor.GRAY + ")");
    }
}
