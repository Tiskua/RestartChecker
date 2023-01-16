package me.tiskua.restart.util;

import me.tiskua.restart.main.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;

public class Files {
    public static File dataFile;
    public static FileConfiguration data;
    public static File configFile;
    public static FileConfiguration config;


    public static void base(Main main) {
        //log.yml
        dataFile = new File(main.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            System.out.println("Created!!!!");
            main.saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(dataFile);

        //config.yml
        configFile = new File(main.getDataFolder(), "config.yml");
        if (!configFile.exists())
            main.saveResource("config.yml", false);

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void saveDataFile() {
        try {
            data.save(dataFile);
        } catch (IOException var1) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save data to data.yml");
        }
    }


    public static void reloadConfig(Main main) {
        if(Files.configFile == null)
            Files.configFile = new File(main.getDataFolder(), "config.yml");

        Files.config = YamlConfiguration.loadConfiguration(Files.configFile);

        InputStream defaultStream2 = main.getResource("config.yml");
        if(defaultStream2 !=  null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream2));
            Files.config.setDefaults(defaultConfig);
        }
    }

    public static void setLastReloadDate() {
        data.set("last_reload", Util.getMilitaryDateFormat().format(new Date()));
        saveDataFile();
    }

    public static void setTotalTimesReloaded() {
        int times = data.getInt("total_reloads") + 1;
        data.set("total_reloads", times);
        saveDataFile();
    }

    public static void setDailyTimesReloaded() {
        int currDay = LocalDate.now().getDayOfMonth();

        int reloadDay = Integer.parseInt(data.getString("last_reload").split(" ")[2]);
        int times = data.getInt("daily_reloads") + 1;

        if(currDay != reloadDay) times = 0;

        data.set("daily_reloads", times);
        saveDataFile();
    }

    public static Date getLastReloadDate() {
        String date = data.getString("last_reload");

        return Util.convertToDate(date, Util.getMilitaryDateFormat());
    }

    public static Integer getTotalTimesReloaded() {
        return data.getInt("total_reloads");
    }

    public static Integer getDailyTimesReloaded() {
        return data.getInt("daily_reloads");
    }

    public static String getReloadType() {
        String reloadType = config.getString("reload_type");
        if (reloadType == null) return "ERROR";
        return config.getString("reload_type");
    }

    public static void checkDataFile() {
        if(data.get("total_reloads") == null) data.set("total_reloads", 0);
        if(data.get("last_reload") == null) data.set("last_reload", "");
        if(data.get("daily_reloads") == null) data.set("daily_reloads", 0);
        if(data.get("was_checking") == null) data.set("was_checking", false);

        saveDataFile();

    }

    public static List<String> getBlacklist() {
        return Files.config.getStringList("blacklist");
    }
    public static List<String> getWhitelist() {
        return Files.config.getStringList("whitelist");
    }
    
}
