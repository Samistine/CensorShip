package com.bw2801.plugins.censorship.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CSCommandListener implements CommandExecutor {

    private final HelpCommand help = new HelpCommand();
    private final ReloadCommand reload = new ReloadCommand();
    private final AddCommand add = new AddCommand();
    private final RemoveCommand remove = new RemoveCommand();
    private final TestCommand test = new TestCommand();
    private final WordsCommand words = new WordsCommand();
    private final PenaltyCommand penalty = new PenaltyCommand();
    private final ClearCommand clear = new ClearCommand();
    private final UpdateCommand update = new UpdateCommand();
    private final ListCommand list = new ListCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (help.onCommand(sender, cmd, label, args)) return true;
        if (reload.onCommand(sender, cmd, label, args)) return true;
        if (add.onCommand(sender, cmd, label, args)) return true;
        if (remove.onCommand(sender, cmd, label, args)) return true;
        if (test.onCommand(sender, cmd, label, args)) return true;
        if (words.onCommand(sender, cmd, label, args)) return true;
        if (penalty.onCommand(sender, cmd, label, args)) return true;
        if (update.onCommand(sender, cmd, label, args)) return true;
        if (clear.onCommand(sender, cmd, label, args)) return true;
        if (list.onCommand(sender, cmd, label, args)) return true;

        sender.sendMessage(ChatColor.RED + "The command you entered could not be found. Use " + ChatColor.GOLD + "/censor help");
        return false;
    }
}
