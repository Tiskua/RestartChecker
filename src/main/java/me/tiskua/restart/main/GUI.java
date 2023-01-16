package me.tiskua.restart.main;

import me.tiskua.restart.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class GUI implements Listener {

    Main main = Main.main;
    Checker checker = main.checker;
    public Inventory gui;

    int taskId;

    public void createMainGUI() {
        gui = Bukkit.createInventory(null, 45, "Info");
        setBorders(gui);

        gui.setItem(20, new ItemCreator(Material.WOOL).setDisplayname("&a&lStart Checking!").setDurability(5).buildItem());

        setInfoItem();

        gui.setItem(24, new ItemCreator(Material.WOOL).setDisplayname("&c&lStop Checking!").setDurability(14).buildItem());
    }

    public void setInfoItem() {
        List<String> lore = new ArrayList<>();
        lore.add(Util.format("&7&l==========================="));
        lore.add(Util.format("&7* &eTimes reloaded (Total)&7: " + Files.getTotalTimesReloaded()));
        lore.add(Util.format("&7* &eTimes reloaded (Daily)&7: " + Files.getDailyTimesReloaded()));
        lore.add(Util.format("&7* &eLast reloaded&7: " +
                (Files.getLastReloadDate() != null ? Files.getLastReloadDate() : "&cUNKNOWN")));
        lore.add(Util.format("&7* &eCurrently Checking&7: " + (checker.isChecking ? "&aYes" : "&cNo")));
        lore.add(Util.format("&7* &eChecking For&7: " + checker.findDuraiton()));
        lore.add(Util.format("&7&l==========================="));

        ItemStack infoItem = new ItemCreator(Material.COMPASS).setDisplayname("&6&lInfo:").lore(lore).buildItem();
        ItemMeta infoMeta = infoItem.getItemMeta();

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            lore.set(lore.size()-2, Util.format("&7* &eChecking For&7: " + checker.findDuraiton()));
            infoMeta.setLore(lore);
            infoItem.setItemMeta(infoMeta);
            gui.setItem(22, infoItem);
        }, 0, 20);
    }

    public void setBorders(Inventory inv) {
        ItemStack innerBorder = new ItemCreator(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(8).buildItem();
        ItemStack outerBorder = new ItemCreator(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).buildItem();

        int size = inv.getSize();

        //Inner Border
        for(int i = 0; i <size; i++) inv.setItem(i, innerBorder);

        //Outer Border
        for(int i =0; i <9; i++) inv.setItem(i, outerBorder);

        for(int i =size-9; i <size; i++) inv.setItem(i, outerBorder);

        for(int i = 1; i < (size/9)-1; i++) {
            inv.setItem(9*i, outerBorder);
            inv.setItem(9*i+8, outerBorder);
        }
    }

    @EventHandler
    public void clickItem(InventoryClickEvent event) {
        if(!event.getInventory().equals(gui)) return;
        event.setCancelled(true);

        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();

        if (slot == 20) {
            if (Util.notHavePermission(player, "restartchecker.start")) return;
            if (!checker.isChecking) {
                player.sendMessage(Util.format(Util.prefix + "&aChanges are now being detected!"));
                checker.startChecking();
            } else player.sendMessage(Util.format(Util.prefix + "&cChanges are already being detected!"));
        }

        if (slot == 24){
            if (Util.notHavePermission(player, "restartchecker.stop")) return;
            if (checker.isChecking) {
                checker.stopChecking();
                player.sendMessage(Util.format(Util.prefix + "&cChanges are no longer being detected!"));
            } else player.sendMessage(Util.format(Util.prefix + "&cChanges were not being detected previously!"));
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        if(!event.getInventory().equals(gui)) return;
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
