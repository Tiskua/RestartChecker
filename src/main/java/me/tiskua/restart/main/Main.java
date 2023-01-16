package me.tiskua.restart.main;
import me.tiskua.restart.util.*;
import me.tiskua.restart.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.*;


public final class Main extends JavaPlugin implements Listener{

    public static Main main;
    public Checker checker;
    public GUI gui;

    @Override
    public void onEnable() {
        main = this;
        checker = new Checker();
        gui = new GUI();
        Files.base(this);
        Files.checkDataFile();

        this.getCommand("Restartchecker").setExecutor(new Commands());
        this.getCommand("Restartchecker").setTabCompleter(new TabComplete());

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(gui, this);

        checkLatestVersion();

        if (Files.data.getBoolean("was_checking")) {
            checker.startChecking();
            for(Player players : Bukkit.getOnlinePlayers()) {
                if (!players.hasPermission("restartchecker.notify")) continue;
                Date now = new Date();
                players.sendMessage(Util.format(Util.prefix + "&eThe server has reloaded (" +
                        Util.getStandardDateFormat().format(now) + ")"));
            }
        }
    }

    @Override
    public void onDisable() {
        Files.data.set("was_checking", checker.isChecking);
        Files.saveDataFile();
        checker.stopChecking();
    }

    private void checkLatestVersion() {
        (new UpdateChecker(this, 106144)).getLatestVersion((version) -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version))
                this.getLogger().log(Level.INFO, "Restart Checker is running the latest version!");
            else {
                this.getLogger().log(Level.WARNING, "------------------------------------------");
                this.getLogger().log(Level.WARNING, " ");
                this.getLogger().log(Level.WARNING, "An update is available for Restart Checker!");
                this.getLogger().log(Level.WARNING, "Download it here: https://www.spigotmc.org/resources/restart-checker.106144/");
                this.getLogger().log(Level.WARNING, " ");
                this.getLogger().log(Level.WARNING, "------------------------------------------");
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event ) {
        Player player = event.getPlayer();
        if (Util.notHavePermission(player, "restartchecker.update")) return;
        (new UpdateChecker(this, 106144)).getLatestVersion((version) -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                player.sendMessage(Util.format("&7------------------------------------------"));
                player.sendMessage(Util.format(" "));
                player.sendMessage(Util.format("&a&lAn update is available for Restart Checker!"));
                player.sendMessage(Util.format("&e&lDownload it here&7: https://www.spigotmc.org/resources/restart-checker.106144/"));
                player.sendMessage(Util.format(" "));
                player.sendMessage(Util.format("&7------------------------------------------"));
            }
        });
    }
}
