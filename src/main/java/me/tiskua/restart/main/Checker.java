package me.tiskua.restart.main;

import me.tiskua.restart.util.*;
import me.tiskua.restart.util.Files;
import org.bukkit.*;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class Checker {

    public boolean isChecking = false;
    int taskId;

    public void startChecking() {
        try {
            long interval = me.tiskua.restart.util.Files.config.getInt("check_interval");
            Path plugins_directory = Paths.get(Main.main.getDataFolder().getAbsolutePath()).getParent();
            WatchService watchService = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = plugins_directory.register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE
            );

            taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.main, () -> {
                for(WatchEvent<?> event : watchKey.pollEvents()) {
                    Path file = plugins_directory.resolve(((Path) event.context()));
                    String filename = file.getFileName().toString();
                    String pluginname = filename.replace(".jar", "");

                    if(!filename.endsWith(".jar")) continue;
                    if(Files.getBlacklist().contains(pluginname)) continue;
                    if(!Files.getWhitelist().contains(pluginname)
                            && Files.getWhitelist().size() > 0
                            && !Files.getWhitelist().get(0).replace(" ", "").equals(""))
                        continue;

                    if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE &&
                            me.tiskua.restart.util.Files.config.getStringList("reload_on").contains("create"))
                        reload();

                    else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY &&
                            me.tiskua.restart.util.Files.config.getStringList("reload_on").contains("modify"))
                        reload();

                }

            }, 0, interval * 20);
            isChecking = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        if (Files.getReloadType().equals("reload")) {
            saveData();
            Bukkit.getServer().reload();
        }
        else if (Files.getReloadType().equals("restart")) {
            saveData();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }
        else {
            Bukkit.getLogger().log(Level.SEVERE, "reload_type is not valid/missing! Use 'restart' or 'reload'");
            Bukkit.getLogger().log(Level.SEVERE, "If it is missing! Type 'reload_type: reload' somewhere in the config file!");
        }
    }

    private void saveData() {
        Files.setLastReloadDate();
        Files.setDailyTimesReloaded();
        Files.setTotalTimesReloaded();
    }

    public String findDuraiton() {
        Date d1 = new Date();


        Date d2 = Files.getLastReloadDate();

        assert d2 != null;
        long difference_In_Time = d1.getTime() - d2.getTime();


        long difference_In_Seconds = (difference_In_Time / 1000) % 60;
        long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
        long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
        long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

        return difference_In_Days + " days, "
                + difference_In_Hours + " hours, "
                + difference_In_Minutes + " minutes, "
                + difference_In_Seconds + " seconds";
    }

    public void stopChecking() {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
        isChecking = false;
    }
}
