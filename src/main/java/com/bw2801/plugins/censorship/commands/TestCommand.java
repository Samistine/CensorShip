package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.CensorUtil;
import com.bw2801.plugins.censorship.Censorship;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length < 4 || !args[0].equalsIgnoreCase("test")) return false;
        if (!Censorship.hasPermission(cs, "censor.test")) return true;

        String method = null;
        for (String m : CensorUtil.getReplaceUtils()) {
            if (args[1].equalsIgnoreCase(m)) {
                method = m;
                break;
            }
        }

        if (method == null) {
            cs.sendMessage(ChatColor.RED + "The given replace method is not availiable.");
            return true;
        }

        String replace = ChatColor.GOLD + "***" + ChatColor.WHITE;
        String source = "";
        for (int i = 3; i < args.length; i++) {
            source += " " + args[i];
        }
        String result = CensorUtil.replace(source.trim(), args[2], method);
        cs.sendMessage(ChatColor.WHITE + "Result: " + result.replaceAll(args[2], replace));

        return true;
    }
}
