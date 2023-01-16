package me.tiskua.restart.command;

import me.tiskua.restart.util.*;
import me.tiskua.restart.main.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

import java.util.*;

public class Commands implements CommandExecutor {

    private final Main main = Main.main;
    private final GUI gui = main.gui;
    private final Checker checker = main.checker;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("restartchecker")) {
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("startChecking")) {
                if (Util.notHavePermission(player, "restartchecker.start")) return false;
                if (!checker.isChecking) {
                    player.sendMessage(Util.format(Util.prefix + "&aChanges are now being detected!"));
                    checker.startChecking();
                } else player.sendMessage(Util.format(Util.prefix + "&cChanges are already being detected!"));
            } else if (args[0].equalsIgnoreCase("stopChecking")) {
                if (Util.notHavePermission(player, "restartchecker.stop")) return false;
                if (checker.isChecking) {
                    checker.stopChecking();
                    player.sendMessage(Util.format(Util.prefix + "&cChanges are no longer being detected!"));
                } else player.sendMessage(Util.format(Util.prefix + "&cChanges were not being detected previously!"));
            } else if (args[0].equalsIgnoreCase("info")) {
                if (Util.notHavePermission(player, "restartchecker.info")) return false;
                player.sendMessage(Util.format("&7&l==========================="));
                player.sendMessage(Util.format("&a&lRestart Info:"));
                player.sendMessage(Util.format("  &7* &eTimes reloaded (Total)&7: " + Files.getTotalTimesReloaded()));
                player.sendMessage(Util.format("  &7* &eTimes reloaded (Daily)&7: " + Files.getDailyTimesReloaded()));
                player.sendMessage(Util.format("  &7* &eLast reloaded&7: " +
                        (Files.getLastReloadDate() != null ? Files.getLastReloadDate() : "&cUNKNOWN")));
                player.sendMessage(Util.format("  &7* &eCurrently Checking&7: " + (checker.isChecking ? "&aYes" : "&cNo")));
                player.sendMessage(Util.format("&7&l==========================="));
            } else if (args[0].equalsIgnoreCase("plugins")) {
                if (Util.notHavePermission(player, "restartchecker.plugins")) return false;
                player.sendMessage(Util.format("&7&l==========================="));
                player.sendMessage(Util.format("&a&lPlugins:"));
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    boolean isEnabled = plugin.isEnabled();
                    String description = plugin.getDescription().getDescription() == null ? "There is no description!" :
                            plugin.getDescription().getDescription();
                    player.sendMessage(Util.format("  &a" + plugin.getName() + "&e: " + description +
                            (isEnabled ? "" : " &c(DISABLED)")));
                }
                player.sendMessage(Util.format("&7&l==========================="));
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (Util.notHavePermission(player, "restartchecker.plugins.disable")) return false;
                if(getPlugin(args[1]) == null) {
                    player.sendMessage(Util.format("&c&lThis is not a valid plugin name!"));
                    player.sendMessage(Util.format("&c&lDo /restartchecker plugins to see the list"));
                    return false;
                }
                String pluginName = Objects.requireNonNull(getPlugin(args[1])).getName();
                player.sendMessage(Util.format(Util.prefix + "&c&lDisabled: " + pluginName));

                Bukkit.getServer().getPluginManager().disablePlugin(getPlugin(args[1]));

            } else if (args[0].equalsIgnoreCase("enable")) {
                if (Util.notHavePermission(player, "restartchecker.plugins.enable")) return false;
                if(getPlugin(args[1]) == null)  {
                    player.sendMessage(Util.format("&c&lThis is not a valid plugin name!"));
                    player.sendMessage(Util.format("&c&lDo /restartchecker plugins to see the list"));
                    return false;
                }
                String pluginName = Objects.requireNonNull(getPlugin(args[1])).getName();
                player.sendMessage(Util.format(Util.prefix + "&a&lEnabled " + pluginName));
                Bukkit.getServer().getPluginManager().enablePlugin(getPlugin(args[1]));
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (Util.notHavePermission(player, "restartchecker.reload")) return false;
                player.sendMessage(Util.format(Util.prefix + "&bReloaded the config file!"));
                Files.reloadConfig(main);
            } else if (args[0].equalsIgnoreCase("gui")) {
                gui.createMainGUI();
                player.openInventory(gui.gui);
            }
        }
        return false;
    }

    private Plugin getPlugin(String p) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equals(p)) {
                Bukkit.getServer().getPluginManager().enablePlugin(plugin);
                return plugin;
            }
        }
        return null;
    }
}
