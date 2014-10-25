package com.bw2801.plugins.censorship;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CSCommandListener implements CommandExecutor {

    private Censorship plugin;

    public CSCommandListener(Censorship plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 5) {
            if (args[0].equals("add")) {
                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String penaltyPoints = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_p = "config.censorship." + word + ".penalty-points";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    plugin.getConfig().addDefault(path_w, replace);
                    plugin.getConfig().addDefault(path_p, penaltyPoints);
                    plugin.getConfig().addDefault(path_a, action);
                    plugin.getConfig().addDefault(path_d, 0);

                    plugin.getConfig().set(path_w, replace);
                    plugin.getConfig().set(path_p, penaltyPoints);
                    plugin.getConfig().set(path_a, action);
                    plugin.getConfig().set(path_d, 0);

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Penalty-Points: '" + ChatColor.GOLD + penaltyPoints + ChatColor.WHITE + "'.");

                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String penaltyPoints = args[4];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_p = "config.censorship." + word + ".penalty-points";
                    String path_a = "config.censorship." + word + ".action";
                    String path_d = "config.censorship." + word + ".damage";

                    plugin.getConfig().addDefault(path_w, replace);
                    plugin.getConfig().addDefault(path_p, penaltyPoints);
                    plugin.getConfig().addDefault(path_a, action);
                    plugin.getConfig().addDefault(path_d, 0);

                    plugin.getConfig().set(path_w, replace);
                    plugin.getConfig().set(path_p, penaltyPoints);
                    plugin.getConfig().set(path_a, action);
                    plugin.getConfig().set(path_d, 0);

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + "' - Penalty-Points: '" + ChatColor.GOLD + penaltyPoints + ChatColor.WHITE + "'.");
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 4) {
            if (args[0].equals("add")) {
                if (args[1].equals("exception")) {
                    if (sender.hasPermission("censor.add")) {
                        String word = args[2];
                        String exec = args[3];
                        String path_w = "config.censorship." + word + ".exceptions";

                        List<String> list = plugin.getConfig().getStringList(path_w);
                        if (!list.contains(exec)) {
                            list.add(exec);
                        } else {
                            sender.sendMessage(ChatColor.RED + "This exceptions was already added.");
                            return true;
                        }

                        plugin.getConfig().addDefault(path_w, list);
                        plugin.getConfig().set(path_w, list);
                        plugin.saveConfig();
                        plugin.reloadConfig();

                        sender.sendMessage("Added Exception '" + ChatColor.GOLD + exec + ChatColor.WHITE + "' for the word '" + ChatColor.GOLD + word + ChatColor.WHITE + "'.");
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                    }

                    return true;
                }

                if (sender.hasPermission("censor.add")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";
                    String path_p = "config.censorship." + word + ".penalty-points";

                    plugin.getConfig().addDefault(path_w, replace);
                    plugin.getConfig().addDefault(path_a, action);
                    plugin.getConfig().addDefault(path_p, 0);
                    plugin.getConfig().set(path_w, replace);
                    plugin.getConfig().set(path_a, action);
                    plugin.getConfig().set(path_p, 0);

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Added '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");
                    } else {
                        sender.sendMessage("Added '" + word + "' - Replace with '" + replace + "' - Action: '" + action + "'.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("update")) {
                if (sender.hasPermission("censor.update")) {
                    String word = args[1];
                    String replace = args[2];
                    String action = args[3];
                    String path_w = "config.censorship." + word + ".replace-with";
                    String path_a = "config.censorship." + word + ".action";

                    plugin.getConfig().addDefault(path_w, replace);
                    plugin.getConfig().addDefault(path_a, action);
                    plugin.getConfig().set(path_w, replace);
                    plugin.getConfig().set(path_a, action);

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    sender.sendMessage("Updated '" + ChatColor.GOLD + word + ChatColor.WHITE + "' - Replace with '" + ChatColor.GOLD + replace + ChatColor.WHITE + "' - Action: '" + ChatColor.GOLD + action + ChatColor.WHITE + ".");

                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 3) {
            if (args[1].equals("add")) {
                if (sender.hasPermission("censor.penalty-points.add")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = plugin.getPlayerConfig().getInt("players.censorship." + target + ".pentalty-points");

                        int out = path_i.intValue() + points;

                        plugin.getConfig().set(path_p, out);
                        plugin.savePlayerConfig();

                        sender.sendMessage("Added '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points to '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[1].equals("remove")) {
                if (sender.hasPermission("censor.penalty-points.remove")) {
                    try {
                        String target = args[0];
                        int points = Integer.parseInt(args[2]);

                        String path_p = "players.censorship." + target + ".penalty-points";
                        Integer path_i = plugin.getPlayerConfig().getInt("players.censorship." + target + ".pentalty-points");

                        int out = path_i.intValue() - points;

                        plugin.getConfig().set(path_p, out);
                        plugin.savePlayerConfig();

                        sender.sendMessage("Removed '" + ChatColor.RED + path_i + ChatColor.WHITE + "' pentalty points from '" + ChatColor.GOLD + target + ChatColor.WHITE + "'.");
                    } catch (Exception ee) {
                        sender.sendMessage("Could not find player or integer.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 2) {
            if (args[0].equals("remove")) {
                if (sender.hasPermission("censor.remove")) {
                    String word = args[1];
                    String path_all = "config.censorship." + word;

                    plugin.getConfig().addDefault(path_all, null);
                    plugin.getConfig().set(path_all, null);

                    plugin.saveConfig();
                    plugin.reloadConfig();

                    if ((sender instanceof Player)) {
                        sender.sendMessage("Removed '" + ChatColor.GOLD + word + ChatColor.WHITE + "'");
                    } else {
                        sender.sendMessage("Removed '" + word + "'");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else if (args.length == 1) {
            if (args[0].equals("reload")) {
                if (sender.hasPermission("censor.reload")) {
                    plugin.reloadConfig();
                    plugin.reloadPlayerConfig();
                    plugin.reloadMessageConfig();
                    sender.sendMessage("CensorShip configurations reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessageConfig().getString("messages.no-permission"));
                }
            } else if (args[0].equals("help")) {
                sender.sendMessage(ChatColor.AQUA + "=== CensorShip - Help ===");
                sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <ban/kick/none> [pp] " + ChatColor.GRAY + "- Add a word to the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception> " + ChatColor.GRAY + "- Add an exception to a word.");
                sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <ban/kick/none> [pp] " + ChatColor.GRAY + "- Changes a word in the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor remove <word> " + ChatColor.GRAY + "- Removes a word from the config.");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points> " + ChatColor.GRAY + "- Adds penalty-points to a player.");
                sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points> " + ChatColor.GRAY + "- Removes penalty-points of a player.");
                sender.sendMessage(ChatColor.GOLD + "/censor reload " + ChatColor.GRAY + "- Reloads the configuration file.");
            } else {
                sender.sendMessage("Use " + ChatColor.GOLD + "/censor help");
            }
        } else {
            sender.sendMessage(ChatColor.AQUA + "=== CensorShip - Help ===");
            sender.sendMessage(ChatColor.GOLD + "/censor add <word> <replace-with> <ban/kick/none> [pp] " + ChatColor.GRAY + "- Add a word to the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor add exception <word> <exception> " + ChatColor.GRAY + "- Add an exception to a word.");
            sender.sendMessage(ChatColor.GOLD + "/censor update <word> <replace-with> <ban/kick/none> [pp] " + ChatColor.GRAY + "- Changes a word in the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor remove <word> " + ChatColor.GRAY + "- Removes a word from the config.");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> add <penalty-points> " + ChatColor.GRAY + "- Adds penalty-points to a player.");
            sender.sendMessage(ChatColor.GOLD + "/censor <player> remove <penalty-points> " + ChatColor.GRAY + "- Removes penalty-points of a player.");
            sender.sendMessage(ChatColor.GOLD + "/censor reload " + ChatColor.GRAY + "- Reloads the configuration file.");
        }
        return false;
    }

}
