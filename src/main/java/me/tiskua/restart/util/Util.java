package me.tiskua.restart.util;

import org.bukkit.*;
import org.bukkit.entity.*;

import java.text.*;
import java.util.*;
import java.util.stream.*;

public class Util {
    public static String prefix = "&7[&a&lRestart Checker&7]&r ";

    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> formatList(List<String> list) {
        return list.stream().map(line->ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
    }
    public static boolean notHavePermission(Player player, String permission) {
        if (!Files.config.getBoolean("use-permissions")) return false;
        if(!player.hasPermission(permission)) {
            player.sendMessage(format("&cYou do not have permission to run this command!"));
            return true;
        }
        return false;
    }

    public static Date convertToDate(String d, SimpleDateFormat format) {
        if (d.equals("")) return null;
        try {
            return format.parse(d);
        } catch (ParseException e) {
            System.out.println("CANNOT CONVERT THE STRING TO A DATE");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isLegacy() {
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        int numVersion = Integer.parseInt(serverVersion.split("_")[1]);

        return numVersion <= 12;

    }

    public static SimpleDateFormat getMilitaryDateFormat() {
        return new SimpleDateFormat("E MMM dd kk:mm:ss yyyy z");
    }

    public static SimpleDateFormat getStandardDateFormat() {
        return new SimpleDateFormat("E MMM dd hh:mm:ss a yyyy z");
    }

}
