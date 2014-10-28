package com.bw2801.plugins.censorship.commands;

import com.bw2801.plugins.censorship.Censorship;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CSCommandListener implements CommandExecutor {

    private final Censorship plugin;

    private final HelpCommand help = new HelpCommand();
    private final ReloadCommand reload = new ReloadCommand();
    private final AddCommand add = new AddCommand();
    private final RemoveCommand remove = new RemoveCommand();

    public CSCommandListener(Censorship plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (help.onCommand(sender, cmd, label, args)) return true;
        if (reload.onCommand(sender, cmd, label, args)) return true;
        if (add.onCommand(sender, cmd, label, args)) return true;
        if (remove.onCommand(sender, cmd, label, args)) return true;

        help.showHelp(sender);
        return false;
    }
}
